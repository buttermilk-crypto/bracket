package asia.redact.bracket.properties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Test;

import asia.redact.bracket.properties.line.Line;
import asia.redact.bracket.properties.line.LineEnding;
import asia.redact.bracket.properties.line.LineScanner;

public class BigPropertiesTest {

	private LineScanner scanner;
	
	@Test
	public void test2() throws IOException{
		File f = new File("./src/test/resources/3props.properties");
		Assert.assertTrue(f.exists());
		long size = f.length();
		InputStream in = getClass().getResourceAsStream("/3props.properties");
		LineScanner scanner = new LineScanner(new InputStreamReader(in),8);
		while(scanner.readLine()!=null){}
		scanner.close();
		Assert.assertEquals(size, scanner.getTotalRead());
	}
	
	@Test
	public void test3() throws IOException{
		File f = new File("./src/test/resources/3props.properties");
		Assert.assertTrue(f.exists());
		long size = f.length();
		InputStream in = getClass().getResourceAsStream("/3props.properties");
		scanner = new LineScanner(new InputStreamReader(in), 8);
		Line line;
		while((line = scanner.line())!=null){
			System.out.println(line);
		}
		Assert.assertEquals(size, scanner.getTotalRead());
	}
	
	@Test
	public void test4() throws IOException{
		File f = new File("./src/test/resources/10props.properties");
		Assert.assertTrue(f.exists());
		long size = f.length();
		InputStream in = getClass().getResourceAsStream("/10props.properties");
		scanner = new LineScanner(new InputStreamReader(in), 8);
		Line line;
		while((line = scanner.line())!=null){
			Assert.assertEquals(LineEnding.CRLF, line.getEnding());
			System.out.println(line);
		}
		Assert.assertEquals(size, scanner.getTotalRead());
	}
	
	@Test
	public void test5() throws IOException{
		File f = new File("./src/test/resources/10props.mac.properties");
		Assert.assertTrue(f.exists());
		long size = f.length();
		InputStream in = getClass().getResourceAsStream("/10props.mac.properties");
		scanner = new LineScanner(new InputStreamReader(in),8);
		Line line;
		while((line = scanner.line())!=null){
			Assert.assertEquals(LineEnding.CR, line.getEnding());
			System.out.println(line);
		}
		Assert.assertEquals(size, scanner.getTotalRead());
	}
	
	@Test
	public void test6() throws IOException{
		File f = new File("./src/test/resources/10props.unix.properties");
		Assert.assertTrue(f.exists());
		long size = f.length();
		InputStream in = getClass().getResourceAsStream("/10props.unix.properties");
		scanner = new LineScanner(new InputStreamReader(in),8);
		Line line;
		while((line = scanner.line())!=null){
			Assert.assertEquals(LineEnding.LF, line.getEnding());
			System.out.println(line);
		}
		Assert.assertEquals(size, scanner.getTotalRead());
	}
	
}
