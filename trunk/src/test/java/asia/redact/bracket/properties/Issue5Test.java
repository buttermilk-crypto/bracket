package asia.redact.bracket.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Test;

import asia.redact.bracket.properties.Properties.Mode;
import asia.redact.bracket.util.AsciiToNativeFilterReader;
import asia.redact.bracket.util.NativeToAsciiFilter;

public class Issue5Test {

	public Issue5Test() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void test1() {
		InputStream in = getClass().getResourceAsStream("/Issue5Resources/test1.properties");
		Assert.assertNotNull(in);
		AsciiToNativeFilterReader reader = new AsciiToNativeFilterReader(new InputStreamReader(in));
		StringBuilder builder = new StringBuilder();
		char [] array = new char[8120];
		int count = 0;
	   try {
		while((count = reader.read(array))!= -1) {
			   builder.append(array,0,count);
		   }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	    
	    System.err.println(builder.toString());
	}
	
	@Test
	public void test0() {
		InputStream in = getClass().getResourceAsStream("/Issue5Resources/test1.properties");
		Assert.assertNotNull(in);
		Properties.Factory.mode = Mode.Compatibility;
		Properties props = Properties.Factory.getInstance(in);
		Assert.assertTrue(props.containsKey("german_umlaute"));
		Assert.assertEquals(5, props.get("german_umlaute").length()); // five chars instead of what was input
	}
	
	@Test
	public void test2() {
		InputStream in = getClass().getResourceAsStream("/Issue5Resources/test1.properties");
		Assert.assertNotNull(in);
		Properties.Factory.mode = Mode.Compatibility;
		Properties props = Properties.Factory.getInstance(in);
		Assert.assertTrue(props.containsKey("german_umlaute"));
		Assert.assertEquals(5, props.get("german_umlaute").length()); // five chars instead of what was input
		
		NativeToAsciiFilter filter = new NativeToAsciiFilter();
		filter.write(props.get("german_umlaute"));
		Assert.assertNotNull(filter.getResult());
		Assert.assertEquals(20, filter.getResult().length());
	}

}
