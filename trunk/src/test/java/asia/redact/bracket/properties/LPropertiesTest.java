package asia.redact.bracket.properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
/**
 * Run the tests in PropertiesTest using the Line mode
 * 
 * @author Dave
 *
 */
public class LPropertiesTest extends PropertiesTest {

	@BeforeClass 
	public static void setModeInFactory() {
	      Properties.Factory.mode = Properties.Mode.Line;
	}
	
	@AfterClass 
	public static void setDefaultModeInFactory() {
		Properties.Factory.mode = Properties.Mode.BasicToken;
	}
}
