package asia.redact.bracket.util;

import org.junit.Test;

public class AsciiTest {

	public AsciiTest() {}
	
	@Test
	public void test0() {
		System.err.println("Platform default: "+Encodings.getDefaultEncoding());
		System.err.println("Platform defaults to Ascii: "+Encodings.platformDefaultsToAScii());
	}

}
