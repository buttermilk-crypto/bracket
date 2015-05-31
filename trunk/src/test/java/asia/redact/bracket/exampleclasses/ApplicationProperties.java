package asia.redact.bracket.exampleclasses;

import java.io.FileInputStream;

import asia.redact.bracket.properties.GroupParams;
import asia.redact.bracket.properties.Properties;
import asia.redact.bracket.properties.PropertiesImpl;
 
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
 
/**
 * Test case from Carlo Tomei
 * 
 * @author Dave
 *
 */
public class ApplicationProperties {
     
      Logger logger = Logger.getLogger(ApplicationProperties.class);
      // logger conf file
      public static final String BPER_IVR_LOG4J  = "BPER_IVR_LOG4J";
 
     
      private static ApplicationProperties instance  = null;
      private Properties properties;
     
      private ApplicationProperties()throws Exception{
            System.out.println("[BPER IVR] Loading Application Configuration");
            initProp();
            System.out.println("[BPER IVR] Loading Application log configuration");
            initLogger();
            System.out.println("[BPER IVR] End Application Configuration");
            logger.info("Log4j is now configured...");
      }
     
      public static ApplicationProperties getInstance()throws Exception {
            if(instance == null){
                  instance = new ApplicationProperties();
            }
            return instance;
      }
     
      private void initProp()throws Exception{
            FileInputStream in = null;
            try {
                  //TODO: Gestire prop inputStream
                  System.out.println("[BPER IVR]   Configuration file: ./src/test/resources/environment.properties" );
                  in = new FileInputStream("./src/test/resources/environment.properties");
                  properties = Properties.Factory.getInstance(in);
               //   in.close();
            } catch (Exception e) {
                  throw e;
            }finally{
                  try {
                        if(in != null)
                             in.close();
                  } catch (Exception e) {}
                  in = null;
            }
      }
     
      private void initLogger(){
            String filePath = getProperty(BPER_IVR_LOG4J);
            System.out.println("[BPER IVR]   Application log configuration file path: "+filePath);
            DOMConfigurator.configureAndWatch(filePath);
      }
     
      public String getProperty(String propertyName){
            return this.properties.get(propertyName);
      }
     
      public Properties getGroup(String propertyName){
            return ((PropertiesImpl)this.properties).getGroup(new GroupParams(propertyName));
      }
}
