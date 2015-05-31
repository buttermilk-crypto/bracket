package asia.redact.bracket.properties;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import junit.framework.Assert;

public class ObfuscateTest {

	@Test
	public void test0() {
		
		String password1 = "password1";
		char [] charPass = password1.toCharArray();
		byte [] bytesPass = password1.getBytes();
		String bytesPassBase64 = Obfuscate.FACTORY.encrypt(bytesPass);
		byte [] resultFromBytes = Obfuscate.FACTORY.decryptToBytes(bytesPassBase64);
		Assert.assertTrue(Arrays.equals(bytesPass, resultFromBytes));
		
		String charPassBase64 = Obfuscate.FACTORY.encrypt(charPass);
		char [] resultsChar = Obfuscate.FACTORY.decryptToChar(charPassBase64);
		Assert.assertTrue(Arrays.equals(charPass, resultsChar));
		
		String stringBase64 = Obfuscate.FACTORY.encrypt(password1);
		String result = Obfuscate.FACTORY.decrypt(stringBase64);
		Assert.assertEquals(password1, result);
		
	}
	
	@Test
	public void test1() {
		
		File file = new File(System.getProperty("user.home")+File.separator+"salt.txt");
		try {
			FileWriter writer = new FileWriter(file);
			writer.write("salt=1234567890");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		String password1 = "password1";
		char [] charPass = password1.toCharArray();
		byte [] bytesPass = password1.getBytes();
		String bytesPassBase64 = Obfuscate.FACTORY.encrypt(bytesPass);
		byte [] resultFromBytes = Obfuscate.FACTORY.decryptToBytes(bytesPassBase64);
		Assert.assertTrue(Arrays.equals(bytesPass, resultFromBytes));
		
		String charPassBase64 = Obfuscate.FACTORY.encrypt(charPass);
		char [] resultsChar = Obfuscate.FACTORY.decryptToChar(charPassBase64);
		Assert.assertTrue(Arrays.equals(charPass, resultsChar));
		
		String stringBase64 = Obfuscate.FACTORY.encrypt(password1);
		String result = Obfuscate.FACTORY.decrypt(stringBase64);
		Assert.assertEquals(password1, result);
		
		file.delete();
		
	}

}
