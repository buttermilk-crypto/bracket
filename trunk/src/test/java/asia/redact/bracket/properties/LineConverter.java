package asia.redact.bracket.properties;


import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import asia.redact.bracket.properties.line.Line;
import asia.redact.bracket.properties.line.LineScanner;

/**
 * Utility to generate test properties files reflecting different line endings
 * 
 * @author Dave
 *
 */
public class LineConverter {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) {
		File resources = new File("./src/test/resources");
		File [] files = resources.listFiles(new FileFilter(){
			public boolean accept(File pathname) {
				if(pathname.getName().endsWith(".properties")) return true;
				return false;
			}
		});
		
		for(int i =0;i<files.length;i++){
			File newFile = new File(files[i].getParentFile(),name(files[i].getName(),".unix"));
			newFile.setWritable(true);
		
			try {
				System.err.println(newFile.getCanonicalPath());
				FileOutputStream out = new FileOutputStream(newFile, false);
			//	BufferedWriter outBuf = new BufferedWriter(new OutputStreamWriter(out));
				InputStreamReader reader = new InputStreamReader(new FileInputStream(files[i]));
				LineScanner scanner = new LineScanner(reader);
				Line line;
				while((line=scanner.line())!=null){
					String outLine = line.getText()+'\n';
					out.write(outLine.getBytes());
				}
				out.flush();
				out.close();
				scanner.close();
			}catch (IOException x){
				x.printStackTrace();
			}
			
		}
		
		for(int i =0;i<files.length;i++){
			File newFile = new File(files[i].getParentFile(),name(files[i].getName(),".mac"));
			newFile.setWritable(true);
		
			try {
				System.err.println(newFile.getCanonicalPath());
				FileOutputStream out = new FileOutputStream(newFile, false);
			//	BufferedWriter outBuf = new BufferedWriter(new OutputStreamWriter(out));
				InputStreamReader reader = new InputStreamReader(new FileInputStream(files[i]));
				LineScanner scanner = new LineScanner(reader);
				Line line;
				while((line=scanner.line())!=null){
					String outLine = line.getText()+'\r';
					out.write(outLine.getBytes());
					
				}
				
				out.flush();
				out.close();
				scanner.close();
			}catch (IOException x){
				x.printStackTrace();
			}
		}
	}
	
	private static String name(String name, String token){
		StringBuffer buf = new StringBuffer();
		int index = name.indexOf(".");
		buf.append(name.substring(0,index));
		buf.append(token);
		buf.append(".properties");
		return buf.toString();
	}

}
