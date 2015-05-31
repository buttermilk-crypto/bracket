package asia.redact.bracket.properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class XMLTest {
	
	@Test
	public void test0() {
		InputStream in = getClass().getResourceAsStream("/xml/small.xml");
		InputStreamReader reader = new InputStreamReader(in);
		BufferedReader breader = new BufferedReader(reader);
		ParseXML parser = new ParseXML();
		parser.parse(breader);
		Properties props = parser.getProps();
		Assert.assertTrue(props.size() > 0);
		Assert.assertEquals("a value", props.get("a.b.c"));
		List<String> comments = props.getComments("a.b.c");
		Assert.assertEquals(1, comments.size());
		Assert.assertEquals("# test comment   ", comments.get(0));
		System.err.println(props);
	}
	
	@Test
	public void test1() {
		InputStream in = getClass().getResourceAsStream("/xml/small2.xml");
		InputStreamReader reader = new InputStreamReader(in);
		BufferedReader breader = new BufferedReader(reader);
		ParseXML parser = new ParseXML();
		parser.parse(breader);
		Properties props = parser.getProps();
		System.err.println(props);
		Assert.assertTrue(props.size() > 0);
		Assert.assertEquals("a value", props.get("a.b.c"));
		Assert.assertEquals("a value", props.get("a.b.d"));
		List<String> comments = props.getComments("a.b.c");
		Assert.assertEquals(1, comments.size());
		Assert.assertEquals("# test comment   ", comments.get(0));
		comments = props.getComments("a.b.d");
		Assert.assertEquals(1, comments.size());
		Assert.assertEquals("# test comment   ", comments.get(0));
		
		
	
	}
	
	@Test
	public void test2() {
		InputStream in = getClass().getResourceAsStream("/xml/PDJLog.xml");
		InputStreamReader reader = new InputStreamReader(in);
		BufferedReader breader = new BufferedReader(reader);
		ParseXML parser = new ParseXML();
		parser.parse(breader);
		Properties props = parser.getProps();
		System.err.println(props);
		Assert.assertTrue(props.size() > 0);
		Assert.assertTrue(props.size() == 10);
	}
	
	@Test
	public void test3() {
		Properties props = Properties.Factory.getInstanceFromXML(new File("./src/test/resources/xml/PDJLog.xml"));
		Assert.assertTrue(props.size() > 0);
		Assert.assertTrue(props.size() == 10);
	}
	
	@Test
	public void test4() {
		InputStream in = getClass().getResourceAsStream("/xml/input0.properties");
		Properties props = Properties.Factory.getInstance(in);
		OutputAdapter outAdapter = new OutputAdapter(props);
		File temp;
		try {
			temp = File.createTempFile("test", "xml");
			FileWriter writer = new FileWriter(temp);
			outAdapter.writeAsXml(writer);
			writer.close();
			
			FileReader reader = new FileReader(temp);
			BufferedReader breader = new BufferedReader(reader);
			String s = null;
			while((s= breader.readLine())!=null){
				System.err.println(s);
			}
			breader.close();
			Properties p2 = Properties.Factory.getInstanceFromXML(temp);
			Assert.assertTrue(p2.size()>0);
			Assert.assertTrue(p2.size()==3);
			
		} catch (IOException e) {
		
		}
		
		
		
	}

}
