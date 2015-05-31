package asia.redact.bracket.properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Run the tests in PropertiesTest using the StreamingToken mode
 * 
 * @author Dave
 *
 */
public class SPropertiesTest extends PropertiesTest {

	@BeforeClass 
	public static void setModeInFactory() {
	      Properties.Factory.mode = Properties.Mode.Compatibility;
	}
	
	@AfterClass 
	public static void setDefaultModeInFactory() {
		Properties.Factory.mode = Properties.Mode.BasicToken;
	}
}
