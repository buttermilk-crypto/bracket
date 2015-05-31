package asia.redact.bracket.properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import junit.framework.Assert;

import org.junit.Test;

import asia.redact.bracket.properties.Properties.Mode;

/**
 * <pre>
 * XML serialization will fail if nodes contain characters not legal (from the point of view of the
 * rules of well-formed XML). 
 * 
 * Rules:
 * 
 * Names can contain letters, numbers, and other characters
 * Names cannot start with a number or punctuation character
 * Names cannot start with the letters xml (or XML, or Xml, etc)
 * Names cannot contain spaces
 * 
 * </pre>
 * @author Dave
 *
 */
public class Issue6Test {

	@Test
	public void test0() {
		
		
		InputStream in = getClass().getResourceAsStream("/Issue6Resources/input0.properties");
		Properties props = Properties.Factory.getInstance(in);
		OutputAdapter outAdapter = new OutputAdapter(props);
		File temp = null;
		FileWriter writer = null;
		try {
			
			try {
				temp = File.createTempFile("test", "xml");
				writer = new FileWriter(temp);
				outAdapter.writeAsXml(writer);
				writer.close();
			}catch(RuntimeException x){
				if(writer != null) {
					writer.close();
				}
				
				Assert.fail("runtime exception, node was unusable for xml element");
				// we've failed as expected. Let this be accepted as the outcome
			//	Assert.assertTrue(true);
				return;
			}
			
		
			FileReader reader = new FileReader(temp);
			BufferedReader breader = new BufferedReader(reader);
			String s = null;
			while((s= breader.readLine())!=null){
				System.err.println(s);
			}
			breader.close();
			Properties p2 = Properties.Factory.getInstanceFromXML(temp);
			Assert.assertTrue(p2.size()>0);
			Assert.assertTrue(p2.size()==5);
			Assert.assertEquals("aVal", p2.get("a.b.c.e.1"));
			
		} catch (IOException e) {
		
		}
	}
	
	
	/**
	 * Input data is encoded as ISO-8859-1. 
	 * 
	 */
	@Test
	public void test1() {

		Charset charset = Charset.forName("ISO-8859-1");
		Properties.Factory.mode = Mode.Compatibility;
		
		InputStream in = getClass().getResourceAsStream("/UTF8Resources/test.properties");
		Assert.assertNotNull(in);
		Properties props = Properties.Factory.getInstance(new InputStreamReader(in,charset));
		OutputAdapter outAdapter = new OutputAdapter(props);
		File temp = null;
		FileWriter writer = null;
		try {
			
			try {
				temp = File.createTempFile("test", "xml");
				writer = new FileWriter(temp);
				outAdapter.writeAsXml(writer);
				writer.close();
			}catch(RuntimeException x){
				if(writer != null) {
					writer.close();
				}
				
				Assert.fail("runtime exception, node was unusable for xml element");
				// we've failed as expected. Let this be accepted as the outcome
			//	Assert.assertTrue(true);
				return;
			}
			
		
			FileReader reader = new FileReader(temp);
			BufferedReader breader = new BufferedReader(reader);
			String s = null;
			while((s= breader.readLine())!=null){
				System.err.println(s);
			}
			breader.close();
			Properties p2 = Properties.Factory.getInstanceFromXML(temp,charset);
			Assert.assertTrue(p2.size()>0);
			Assert.assertTrue(p2.size()==2);
			Assert.assertEquals(17, p2.get("val1").length());
			
		} catch (IOException e) {
		
		}
	}
	
	
	/**
	 * Input data is encoded as UTF-8. 
	 * 
	 */
	@Test
	public void test2() {

		Charset charset = Charset.forName("UTF-8");
		Properties.Factory.mode = Mode.Line; //other than Compatibility
		
		InputStream in = getClass().getResourceAsStream("/UTF8Resources/test.utf8"); //utf-8 encoded properties file
		Assert.assertNotNull(in);
		Properties props = Properties.Factory.getInstance(new InputStreamReader(in,charset));
		OutputAdapter outAdapter = new OutputAdapter(props);
		File temp = null;
		FileWriter writer = null;
		try {
			
			try {
				temp = File.createTempFile("test", "xml");
				writer = new FileWriter(temp);
				outAdapter.writeAsXml(writer);
				writer.close();
			}catch(RuntimeException x){
				if(writer != null) {
					writer.close();
				}
				
				Assert.fail("runtime exception, node was unusable for xml element");
				// we've failed as expected. Let this be accepted as the outcome
			//	Assert.assertTrue(true);
				return;
			}
			
		
			FileReader reader = new FileReader(temp);
			BufferedReader breader = new BufferedReader(reader);
			String s = null;
			while((s= breader.readLine())!=null){
				System.err.println(s);
			}
			breader.close();
			Properties p2 = Properties.Factory.getInstanceFromXML(temp,charset);
			Assert.assertTrue(p2.size()>0);
			Assert.assertTrue(p2.size()==1);
			Assert.assertEquals(17, p2.get("val1").length());
			
		} catch (IOException e) {
		
		}
	}
	
	
	
}
