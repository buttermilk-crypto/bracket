package asia.redact.bracket.properties;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import asia.redact.bracket.properties.PropertiesToken;
import asia.redact.bracket.properties.line.Line;
import asia.redact.bracket.properties.line.LineScanner;
/**
 * <pre>
 * This lexer uses the LineScanner to build a list of tokens suitable for use by PropertiesParser. 
 * 
 * Scan a properties file conforming to the description at
 * http://download.oracle.com/javase/6/docs/api/java/util/Properties.html#load(java.io.Reader)
 * into tokens.
 * 
 * There is one additional extension: a comment line which starts with #;; is treated 
 * as transient (not read in). This can be used later to generate a transient header and footer
 * 
 * </pre>
 * 
 * @author Dave
 *
 */
public class PropertiesStreamingLexer {
	
	final LineScanner scanner;
	final List<PropertiesToken> list;

	public PropertiesStreamingLexer(Reader in) {
		super();
		scanner = new LineScanner(in);
		list = new ArrayList<PropertiesToken>();
	}
	
	public PropertiesStreamingLexer(InputStream in) {
		super();
		scanner = new LineScanner(new InputStreamReader(in));
		list = new ArrayList<PropertiesToken>();
	}
	
	public void lex(){
		try {
			Line line = null;
			while((line = scanner.line())!=null){
				if(line.isPrivateComment())continue;
				list.addAll(line.tokens());
			}
		}finally{
			list.add(PropertiesToken.eof());
		}
	}

	public List<PropertiesToken> getList() {
		return list;
	}
	
}
