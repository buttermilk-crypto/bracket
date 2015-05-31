package asia.redact.bracket.properties;

import java.io.Serializable;
import java.util.List;

/**
 * As of version 1.3.2, ValueModel is an interface. 
 * 
 * @author Dave
 * @see BasicValueModel
 * @see UnsettableValueModel
 */
public interface ValueModel extends Serializable {

	public char getSeparator();

	public List<String> getComments();

	public List<String> getValues();

	public String getValue();
	
	public String toXML(int level);
	
	/**
	 * Output a reasonable representation of what the text for this key value pair would look like
	 * 
	 * @param key
	 * @return
	 */
	public String asKeyValueRep(String key);
	
	public ValueModel cloneImmutable();

}