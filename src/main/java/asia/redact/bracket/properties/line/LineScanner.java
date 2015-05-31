/*
 *  This file is part of Bracket Properties
 *  Copyright 2011 David R. Smith
 *
 */
package asia.redact.bracket.properties.line;

import java.io.IOException;
import java.io.Reader;

/**
 * <pre>
 * 
 * This is an augmented version of java.io.BufferedReader from the Apache Harmony Java implementation. Source:
 * http://www.java2s.com/Open-Source/Java-Document/Apache-Harmony-Java-SE/java-package/java/io/BufferedReader.java.htm
 * 
 * Scan a properties file conforming to the description at
 * http://download.oracle.com/javase/6/docs/api/java/util/Properties.html#load(java.io.Reader)
 * into tokens.
 * 
 * The idea here is that as we read lines, we return not just the line text but a Line object which
 * has knowledge of its internal parts, such as if the line has a key, etc. This allows for streaming
 * parsing and hopefully can handle very large properties files.
 * 
 * 
 * There is one additional extension: a comment line which starts with #;; is treated 
 * as transient (not read in). This is used later to generate a transient header and footer
 * 
 * This class tracks the absolute offset of lines in the file, and the line() method works like
 * readLine() but returns extra info in addition to the String. The Line object returned
 * by "line()" provides tokenization methods and also an absolute index of the line into the file.
 * PropertiesParser2 "pulls" the lines using this BufferedReader implementation.
 * 
 * Note: because this is a Reader, you must call close() on it when done.
 * 
 * </pre>
 * 
 * @see Line
 */
public class LineScanner extends Reader {

    private final Reader in;

    /**
     * The characters that can be read and refilled in bulk. We maintain three
     * indices into this buffer:<pre>
     *     { X X X X X X X X X X X X - - }
     *           ^     ^             ^
     *           |     |             |
     *         mark   pos           end</pre>
     * Pos points to the next readable character. End is one greater than the
     * last readable character. When {@code pos == end}, the buffer is empty and
     * must be {@link #fillBuf() filled} before characters can be read.
     *
     * <p>Mark is the value pos will be set to on calls to {@link #reset}. Its
     * value is in the range {@code [0...pos]}. If the mark is {@code -1}, the
     * buffer cannot be reset.
     *
     * <p>MarkLimit limits the distance between the mark and the pos. When this
     * limit is exceeded, {@link #reset} is permitted (but not required) to
     * throw an exception. For shorter distances, {@link #reset} shall not throw
     * (unless the reader is closed).
     */
    private char[] buf;
    private int pos;
    private int end;
    private int mark = -1;
    private int markLimit = -1;
    private long totalRead=0;
    private int delimiterLength;
    private LineEnding delimiter;

    /**
     * Constructs a new BufferedReader on the Reader {@code in}. The
     * buffer gets the default size (8 KB).
     * 
     * @param in
     *            the Reader that is buffered.
     */
    public LineScanner(Reader in) {
        super (in);
        this.in = in;
        buf = new char[8192];
    }

    /**
     * Constructs a new BufferedReader on the Reader {@code in}. The buffer
     * size is specified by the parameter {@code size}.
     * 
     * @param in
     *            the Reader that is buffered.
     * @param size
     *            the size of the buffer to allocate.
     * @throws IllegalArgumentException
     *             if {@code size <= 0}.
     */
    public LineScanner(Reader in, int size) {
        super (in);
        if (size <= 0) {
            throw new IllegalArgumentException("buf must be given a size");
        }
        this.in = in;
        buf = new char[size];
    }

    /**
     * Closes this reader. This implementation closes the buffered source reader
     * and releases the buffer. Nothing is done if this reader has already been
     * closed.
     * 
     * @throws IOException
     *             if an error occurs while closing this reader.
     */
    @Override
    public void close() throws IOException {
        synchronized (lock) {
            if (!isClosed()) {
                in.close();
                buf = null;
            }
        }
    }

    /**
     * Populates the buffer with data. It is an error to call this method when
     * the buffer still contains data; ie. if {@code pos < end}.
     *
     * @return the number of bytes read into the buffer, or -1 if the end of the
     *      source stream has been reached.
     */
    private int fillBuf() throws IOException {
        // assert(pos == end);

        if (mark == -1 || (pos - mark >= markLimit)) {
            /* mark isn't set or has exceeded its limit. use the whole buffer */
            int result = in.read(buf, 0, buf.length);
            if (result > 0) {
                mark = -1;
                pos = 0;
                end = result;
            }
            return result;
        }

        if (mark == 0 && markLimit > buf.length) {
            /* the only way to make room when mark=0 is by growing the buffer */
            int newLength = buf.length * 2;
            if (newLength > markLimit) {
                newLength = markLimit;
            }
            char[] newbuf = new char[newLength];
            System.arraycopy(buf, 0, newbuf, 0, buf.length);
            buf = newbuf;
        } else if (mark > 0) {
            /* make room by shifting the buffered data to left mark positions */
            System.arraycopy(buf, mark, buf, 0, buf.length - mark);
            pos -= mark;
            end -= mark;
            mark = 0;
        }

        /* Set the new position and mark position */
        int count = in.read(buf, pos, buf.length - pos);
        if (count != -1) {
            end += count;
        }
        return count;
    }

    /**
     * Indicates whether or not this reader is closed.
     * 
     * @return {@code true} if this reader is closed, {@code false}
     *         otherwise.
     */
    private boolean isClosed() {
        return buf == null;
    }

    /**
     * Sets a mark position in this reader. The parameter {@code markLimit}
     * indicates how many characters can be read before the mark is invalidated.
     * Calling {@code reset()} will reposition the reader back to the marked
     * position if {@code markLimit} has not been surpassed.
     * 
     * @param markLimit
     *            the number of characters that can be read before the mark is
     *            invalidated.
     * @throws IllegalArgumentException
     *             if {@code markLimit < 0}.
     * @throws IOException
     *             if an error occurs while setting a mark in this reader.
     * @see #markSupported()
     * @see #reset()
     */
    @Override
    public void mark(int markLimit) throws IOException {
        if (markLimit < 0) {
            throw new IllegalArgumentException();
        }
        synchronized (lock) {
            if (isClosed()) {
                throw new IOException("Closed reader");
            }
            this .markLimit = markLimit;
            mark = pos;
        }
    }

    /**
     * Indicates whether this reader supports the {@code mark()} and
     * {@code reset()} methods. This implementation returns {@code true}.
     * 
     * @return {@code true} for {@code BufferedReader}.
     * @see #mark(int)
     * @see #reset()
     */
    @Override
    public boolean markSupported() {
        return true;
    }

    /**
     * Reads a single character from this reader and returns it with the two
     * higher-order bytes set to 0. If possible, BufferedReader returns a
     * character from the buffer. If there are no characters available in the
     * buffer, it fills the buffer and then returns a character. It returns -1
     * if there are no more characters in the source reader.
     *
     * @return the character read or -1 if the end of the source reader has been
     *         reached.
     * @throws IOException
     *             if this reader is closed or some other I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        synchronized (lock) {
            if (isClosed()) {
                throw new IOException("Buffer closed"); //$NON-NLS-1$
            }
            /* Are there buffered characters available? */
            if (pos < end || fillBuf() != -1) {
            	totalRead++;
                return buf[pos++];
            }
            return -1;
        }
    }

    /**
     * Reads at most {@code length} characters from this reader and stores them
     * at {@code offset} in the character array {@code buffer}. Returns the
     * number of characters actually read or -1 if the end of the source reader
     * has been reached. If all the buffered characters have been used, a mark
     * has not been set and the requested number of characters is larger than
     * this readers buffer size, BufferedReader bypasses the buffer and simply
     * places the results directly into {@code buffer}.
     * 
     * @param buffer
     *            the character array to store the characters read.
     * @param offset
     *            the initial position in {@code buffer} to store the bytes read
     *            from this reader.
     * @param length
     *            the maximum number of characters to read, must be
     *            non-negative.
     * @return number of characters read or -1 if the end of the source reader
     *         has been reached.
     * @throws IndexOutOfBoundsException
     *             if {@code offset < 0} or {@code length < 0}, or if
     *             {@code offset + length} is greater than the size of
     *             {@code buffer}.
     * @throws IOException
     *             if this reader is closed or some other I/O error occurs.
     */
    @Override
    public int read(char[] buffer, int offset, int length)
            throws IOException {
        synchronized (lock) {
            if (isClosed()) {
                throw new IOException(""); //$NON-NLS-1$
            }
            if (offset < 0 || offset > buffer.length - length
                    || length < 0) {
                throw new IndexOutOfBoundsException();
            }
            int outstanding = length;
            while (outstanding > 0) {

                /*
                 * If there are bytes in the buffer, grab those first.
                 */
                int available = end - pos;
                if (available > 0) {
                    int count = available >= outstanding ? outstanding
                            : available;
                    System.arraycopy(buf, pos, buffer, offset, count);
                    pos += count;
                    offset += count;
                    outstanding -= count;
                }

                /*
                 * Before attempting to read from the underlying stream, make
                 * sure we really, really want to. We won't bother if we're
                 * done, or if we've already got some bytes and reading from the
                 * underlying stream would block.
                 */
                if (outstanding == 0
                        || (outstanding < length && !in.ready())) {
                    break;
                }

                // assert(pos == end);

                /*
                 * If we're unmarked and the requested size is greater than our
                 * buffer, read the bytes directly into the caller's buffer. We
                 * don't read into smaller buffers because that could result in
                 * a many reads.
                 */
                if ((mark == -1 || (pos - mark >= markLimit))
                        && outstanding >= buf.length) {
                    int count = in.read(buffer, offset, outstanding);
                    if (count > 0) {
                        offset += count;
                        outstanding -= count;
                        mark = -1;
                    }

                    break; // assume the source stream gave us all that it could
                }

                if (fillBuf() == -1) {
                    break; // source is exhausted
                }
            }

            int count = length - outstanding;
            int retVal = (count > 0 || count == length) ? count : -1;
            totalRead+=retVal;
            return retVal;
        }
    }

    public Line line(){
    	 synchronized (lock) {
	    	try {
				String text = readLine();
				if(text != null){
					long startIndex = totalRead-text.length()-delimiterLength;
					return new Line(text,startIndex,delimiter);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
    	 }
    }

    /**
     * Returns the next line of text available from this reader. A line is
     * represented by zero or more characters followed by {@code '\n'},
     * {@code '\r'}, {@code "\r\n"} or the end of the reader. The string does
     * not include the newline sequence.
     * 
     * @return the contents of the line or {@code null} if no characters were
     *         read before the end of the reader has been reached.
     * @throws IOException
     *             if this reader is closed or some other I/O error occurs.
     */
    public String readLine() throws IOException {
        synchronized (lock) {
            if (isClosed()) {
                throw new IOException("Buffer closed"); 
            }
            /* has the underlying stream been exhausted? */
            if (pos == end && fillBuf() == -1) {
                return null;
            }
            for (int charPos = pos; charPos < end; charPos++) {
                char ch = buf[charPos];
                if (ch > '\r') {
                    continue;
                }
                if (ch == '\n') {
                    String res = new String(buf, pos, charPos - pos);
                    pos = charPos + 1;
                    totalRead+=res.length()+1;
                    delimiterLength=1;
                    delimiter=LineEnding.LF;
                    return res;
                } else if (ch == '\r') {
                    String res = new String(buf, pos, charPos - pos);
                    pos = charPos + 1;
                    totalRead+=res.length()+1;
                    delimiterLength=1;
                    delimiter=LineEnding.CR;
                    if (((pos < end) || (fillBuf() != -1))
                            && (buf[pos] == '\n')) {
                    	totalRead++;
                    	delimiterLength=2;
                    	delimiter=LineEnding.CRLF;
                        pos++;
                    }
                    return res;
                }
            }

            char eol = '\0';
            StringBuilder result = new StringBuilder(80);
            /* Typical Line Length */

            result.append(buf, pos, end - pos);
            while (true) {
                pos = end;

                /* Are there buffered characters available? */
                if (eol == '\n') {
                	totalRead+=result.length()+1;
                	delimiterLength=1;
                	delimiter=LineEnding.LF;
                    return result.toString();
                }
                // attempt to fill buffer
                if (fillBuf() == -1) {
                    // characters or null.
                    String tmp = result.length() > 0 || eol != '\0' ? result.toString() : null;
                    if(tmp!=null) totalRead+=result.length();
                    delimiterLength=1;
                    if(eol=='\r')delimiter=LineEnding.CR;
                    else delimiter=LineEnding.LF;
                    return tmp;
                }
                for (int charPos = pos; charPos < end; charPos++) {
                    char c = buf[charPos];
                    if (eol == '\0') {
                        if ((c == '\n' || c == '\r')) {
                            eol = c;
                        }
                    } else if (eol == '\r' && c == '\n') {
                        if (charPos > pos) {
                            result.append(buf, pos, charPos - pos - 1);
                        }
                        pos = charPos + 1;
                        totalRead+=result.length()+2;
                        delimiterLength=2;
                        delimiter=LineEnding.CRLF;
                        return result.toString();
                    } else {
                    	
                        if (charPos > pos) {
                            result.append(buf, pos, charPos - pos - 1);
                        }
                        pos = charPos;
                        totalRead+=result.length()+1;
                        delimiterLength=1;
                      
                       if(eol=='\r') delimiter=LineEnding.CR;
                       else delimiter=LineEnding.LF;
                       return result.toString();
                    }
                }
                if (eol == '\0') {
                    result.append(buf, pos, end - pos);
                } else {
                    result.append(buf, pos, end - pos - 1);
                }
            }
        }

    }

    /**
     * Indicates whether this reader is ready to be read without blocking.
     *
     * @return {@code true} if this reader will not block when {@code read} is
     *         called, {@code false} if unknown or blocking will occur.
     * @throws IOException
     *             if this reader is closed or some other I/O error occurs.
     * @see #read()
     * @see #read(char[], int, int)
     * @see #readLine()
     */
    @Override
    public boolean ready() throws IOException {
        synchronized (lock) {
            if (isClosed()) {
                throw new IOException("Buffer closed");
            }
            return ((end - pos) > 0) || in.ready();
        }
    }

    /**
     * Resets this reader's position to the last {@code mark()} location.
     * Invocations of {@code read()} and {@code skip()} will occur from this new
     * location.
     * 
     * @throws IOException
     *             if this reader is closed or no mark has been set.
     * @see #mark(int)
     * @see #markSupported()
     */
    @Override
    public void reset() throws IOException {
        synchronized (lock) {
            if (isClosed()) {
                throw new IOException("Buffer closed"); 
            }
            if (mark == -1) {
                throw new IOException("mark == -1"); 
            }
            pos = mark;
        }
    }

    /**
     * Skips {@code amount} characters in this reader. Subsequent
     * {@code read()}s will not return these characters unless {@code reset()}
     * is used. Skipping characters may invalidate a mark if {@code markLimit}
     * is surpassed.
     * 
     * @param amount
     *            the maximum number of characters to skip.
     * @return the number of characters actually skipped.
     * @throws IllegalArgumentException
     *             if {@code amount < 0}.
     * @throws IOException
     *             if this reader is closed or some other I/O error occurs.
     * @see #mark(int)
     * @see #markSupported()
     * @see #reset()
     */
    @Override
    public long skip(long amount) throws IOException {
        if (amount < 0) {
            throw new IllegalArgumentException();
        }
        synchronized (lock) {
            if (isClosed()) {
                throw new IOException(""); //$NON-NLS-1$
            }
            if (amount < 1) {
                return 0;
            }
            if (end - pos >= amount) {
                pos += amount;
                return amount;
            }

            long read = end - pos;
            pos = end;
            while (read < amount) {
                if (fillBuf() == -1) {
                    return read;
                }
                if (end - pos >= amount - read) {
                    pos += amount - read;
                    return amount;
                }
                // Couldn't get all the characters, skip what we read
                read += (end - pos);
                pos = end;
            }
            totalRead+=amount;
            return amount;
        }
    }

	public long getTotalRead() {
		return totalRead;
	}

	public LineEnding getDelimiter() {
		return delimiter;
	}
	
  
}