package asia.redact.bracket.properties;

import java.util.ArrayList;
import java.util.List;

import asia.redact.bracket.properties.Properties.Mode;
import asia.redact.bracket.util.NativeToAsciiFilter;

/**
 * Immutable model
 * 
 * @author Dave
 *
 */
public class ImmutableValueModel implements ValueModel {

	private static final long serialVersionUID = 1L;
	
	private final char separator;
	private final String [] values;
	private final Comment [] comments;

	/**
	 * Produces an empty or null model
	 */
	public ImmutableValueModel() {
		separator = '=';
		values = new String[0];
		comments = new Comment[0];
	}
	
	public ImmutableValueModel(String ...values){
		separator = '=';
		this.values = values;
		comments = new Comment[0];
	}
	
	public ImmutableValueModel(Comment [] comments, String ...values){
		separator = '=';
		this.values = values;
		this.comments = comments;
	}
	
	public ImmutableValueModel(Comment [] comments, char sep, String ...values){
		separator = sep;
		this.values = values;
		this.comments = comments;
	}
	
	public ImmutableValueModel(List<String> comments, char sep, List<String> values){
		separator = sep;
		
		this.values = new String[values.size()];
		this.comments = new Comment[comments.size()];
		int i = 0;
		for(String val : values){
			this.values[i] = val;
			i++;
		}
		i = 0;
		for(String val : comments){
			this.comments[i] = new Comment(val);
			i++;
		}
	}

	@Override
	public char getSeparator() {
		return this.separator;
	}

	@Override
	public List<String> getComments() {
		List<String> list = new ArrayList<String>();
		for(Comment c: comments){
			list.add(c.comment);
		}
		return list;
	}

	@Override
	public List<String> getValues() {
		List<String> list = new ArrayList<String>();
		for(String s: values){
			list.add(s);
		}
		return list;
	}

	public String getValue(){
		StringBuilder b = new StringBuilder();
		for(String value:values) b.append(value);
		return b.toString();
	}
	
	public String toString(){
		return getValue();
	}

	public String toXML(int spaces) {
		
		if(Properties.Factory.mode == Mode.Compatibility){
			return toXMLCompatibilityMode(spaces);
		}
		
		StringBuilder sp = new StringBuilder();
		for(int i=0;i<=spaces;i++)sp.append(" ");
		
		StringBuilder builder = new StringBuilder();
		
		for(Comment comment: comments){
			builder.append(sp.toString());
			builder.append("<c><![CDATA[").append(comment).append("]]></c>").append(lineSeparator);
		}
		
		builder.append(sp.toString());
		builder.append("<s>").append(separator).append("</s>").append(lineSeparator);
		
		for(String value: values){
			builder.append(sp.toString());
			if(value != null && (!value.equals(""))) {
				builder.append("<v><![CDATA[").append(value).append("]]></v>").append(lineSeparator);
			}else{
				builder.append("<v/>").append(lineSeparator);
			}
			
		}
		
		return builder.toString();
	}
	
	final static String lineSeparator = System.getProperty("line.separator");
	
	protected String toXMLCompatibilityMode(int spaces) {
		
		StringBuilder sp = new StringBuilder();
		for(int i=0;i<=spaces;i++)sp.append(" ");
		
		StringBuilder builder = new StringBuilder();
		
		for(Comment comment: comments){
			builder.append(sp.toString());
			String encoded = new NativeToAsciiFilter(comment.comment).getResult();
			builder.append("<c><![CDATA[").append(encoded).append("]]></c>").append(lineSeparator);
		}
		
		builder.append(sp.toString());
		// TODO is there a possibility of an empty or not set char for the sep?
		builder.append("<s>").append(separator).append("</s>").append(lineSeparator);
		
		for(String value: values){
			builder.append(sp.toString());
			if(value != null && (!value.equals(""))) {
				String encoded = new NativeToAsciiFilter(value).getResult();
				builder.append("<v><![CDATA[").append(encoded).append("]]></v>").append(lineSeparator);
			}else{
				// values can be the empty string, legal
				builder.append("<v/>").append(lineSeparator);
			}
			
		}
		
		return builder.toString();
	}

	public String asKeyValueRep(String key){
		StringBuffer buf = new StringBuffer();
		if(comments.length > 0){
			for(Comment com: comments){
				buf.append(com.comment);
				buf.append(lineSeparator);
			}
		}
		buf.append(key);
		buf.append(separator);
		int count = 0;
		if(values.length == 0){
			// output nothing
		}else if(values.length == 1){
			for(String val: values){
				buf.append(val);
				buf.append(lineSeparator);
		
			}
		}else {
			for(String val: values){
				buf.append(val);
			    if(count<values.length-1) buf.append("\\");
				buf.append(lineSeparator);
				count++;
			}
		}
		
		return buf.toString();
	}

	@Override
	public ValueModel cloneImmutable() {
		return new ImmutableValueModel(comments, separator, values);
	}

}
