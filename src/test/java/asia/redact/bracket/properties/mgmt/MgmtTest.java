package asia.redact.bracket.properties.mgmt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import asia.redact.bracket.properties.Properties;

public class MgmtTest {

	@Test
	public void test0() {
		
		// some externalized properties in user.home
		String home = System.getProperty("user.home");
		String testExternalProps = home+File.separator+"test.properties";
		String testExternalPropsOb = home+File.separator+"obfuscated.properties";
		
		// some properties loaded from a classpath embedded config file
		String templateProps = "/ExternalizedFiles1/test1.properties";
		
		// some directly passed in properties
		String directProps = "key3=some value\nkey4=another ";
		
		// an override from the command line (entered there with -Dmykey="some value")
		String overrideProp = "mykey";
		
		List<PropertiesReference> refs = new ArrayList<PropertiesReference>();
		refs.add(new PropertiesReference(ReferenceType.CLASSLOADED,templateProps));
		refs.add(new PropertiesReference(ReferenceType.EXTERNAL,testExternalProps));
		refs.add(new PropertiesReference(ReferenceType.DIRECT,directProps));
		refs.add(new PropertiesReference(ReferenceType.COMMANDLINE_OVERRIDE,overrideProp));
		refs.add(new PropertiesReference(ReferenceType.OBFUSCATED,testExternalPropsOb));
		
		// simulate java command line arg
		System.setProperty("mykey", "test");
		
		Properties props = Properties.Factory.loadReferences(refs);
	//	Assert.assertEquals("value1", props.get("key0")); // from external, needs to be locally defined to work
		Assert.assertEquals("val1",props.get("key1")); // from classpath
		Assert.assertEquals("some value", props.get("key3")); //from direct
		Assert.assertEquals("test", props.get("mykey"));
	//	Assert.assertEquals("password1",props.get("password"));
		
	}

}
