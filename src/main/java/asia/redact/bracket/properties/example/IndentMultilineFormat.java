package asia.redact.bracket.properties.example;

import java.util.List;

import asia.redact.bracket.properties.OutputFormat;

/**
 * Example of how to customize an output, perhaps for presentation layer use
 * 
 * @author Dave
 *
 */
public class IndentMultilineFormat implements OutputFormat {

	final static String lineSeparator = System.getProperty("line.separator");
	
	int tabCount;
	char tabChar;
	
	public IndentMultilineFormat(int tabCount, char tabChar) {
		super();
		this.tabCount = tabCount;
		this.tabChar = tabChar;
	}
	
	public String formatContentType() { return ""; }
	
	public String formatHeader() { return ""; }
	
	public String format(String key, char separator, List<String> values, List<String> comments) {
		if(key == null) throw new RuntimeException("Key cannot be null in a format");
		StringBuffer buf = new StringBuffer();
		for(String c: comments){
			buf.append(c);
			buf.append(lineSeparator);
		}
	    StringBuilder keyBuilder=new StringBuilder();
	    for(int i=0;i<key.length();i++){
	    	char ch = key.charAt(i);
	    	if(ch==':'||ch=='='){
	    		keyBuilder.append('\\');	
	    	}
	    	keyBuilder.append(ch);
	    }
		buf.append(keyBuilder.toString());
		buf.append(separator);
		int count =  values.size();
		int i = 0;
		for(String s: values){
			// **********************************************
			// special formatting for multiline values, skip the first value though
			if(i!=0) {
				for(int tc = 0;tc<tabCount;tc++){
					buf.append(tabChar);
				}
				buf.append(" ");
				buf.append(s);
			}else{
				buf.append(s);
			}
			
			// ***********************************************
			
			if(i<count-1) {
				buf.append('\\');
			}
			buf.append(lineSeparator);
			i++;
		}
		
		return buf.toString();
	}

	public String formatFooter() { return ""; }
	

}
