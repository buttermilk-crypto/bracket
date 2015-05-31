package asia.redact.bracket.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import asia.redact.bracket.exampleclasses.TestBean;
import asia.redact.bracket.properties.Properties;

public class SetterTest {
	
	@Test
	public void test1() {
		InputStream in = getClass().getResourceAsStream("/ExtendedAccessors/test0.properties");
		Assert.assertNotNull(in);
		Properties props = Properties.Factory.getInstance(in);
		TestBean bean0 = (TestBean) props.beanValue(TestBean.class, "test.bean.0");
		Assert.assertEquals("my string",bean0.getS1());
		TestBean bean1 = (TestBean) props.beanValue(TestBean.class, "test.bean.1");
		Assert.assertEquals(1000000000000000L,bean1.getLong1());
		
	}

	@Test
	public void test0() {
		TestBean bean = new TestBean();
		AccessorMethodSetter setter = new AccessorMethodSetter(TestBean.class, bean, "s1", "my string");
		try {
			setter.set();
			if(setter.success()){
				Assert.assertEquals("my string", bean.getS1());
			}else{
				Assert.fail("Failed: "+bean.getS1());
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		try {
			
			setter = new AccessorMethodSetter(TestBean.class, bean, "password1", "my pass");
			setter.set();
			if(setter.success()){
				Assert.assertEquals("my pass".length(), bean.getPassword1().length);
			}else{
				Assert.fail("Failed: "+bean.getPassword1().toString());
			}
			
			setter = new AccessorMethodSetter(TestBean.class, bean, "int1", String.valueOf(10));
			setter.set();
			if(setter.success()){
				Assert.assertEquals(10, bean.getInt1());
			}else{
				Assert.fail("Failed: "+bean.getInt1());
			}
			
			setter = new AccessorMethodSetter(TestBean.class, bean, "float1", "10.0f");
			setter.set();
			if(setter.success()){
				Assert.assertEquals(10.0f, bean.getFloat1());
			}else{
				Assert.fail("Failed: "+bean.getFloat1());
			}
			
			setter = new AccessorMethodSetter(TestBean.class, bean, "long1", "1");
			setter.set();
			if(setter.success()){
				Assert.assertEquals(1L, bean.getLong1());
			}else{
				Assert.fail("Failed: "+bean.getLong1());
			}
			
			Date d = new Date();
			long time = d.getTime();
			
			setter = new AccessorMethodSetter(TestBean.class, bean, "date1", String.valueOf(time));
			setter.set();
			if(setter.success()){
				Assert.assertEquals(d, bean.getDate1());
			}else{
				Assert.fail("Failed: "+bean.getDate1());
			}
			
			setter = new AccessorMethodSetter(TestBean.class, bean, "list1", "First,Second,Third,Fourth,Fifth");
			setter.setListDelimiter(",");
			setter.set();
			if(setter.success()){
				Assert.assertEquals(5, bean.getList1().size());
			}else{
				Assert.fail("Failed: "+bean.getList1());
			}
			
			setter = new AccessorMethodSetter(TestBean.class, bean, "list2", "10,20,30,40,50");
			setter.setListDelimiter(",");
			setter.set();
			if(setter.success()){
				Assert.assertEquals(5, bean.getList2().size());
				Assert.assertEquals(new Integer(50), bean.getList2().get(4));
			}else{
				Assert.fail("Failed: "+bean.getList2());
			}
			
			setter = new AccessorMethodSetter(TestBean.class, bean, "bool1", "true");
			setter.set();
			if(setter.success()){
			Assert.assertEquals(true,bean.isBool1());
			}else{
				Assert.fail("Failed: "+bean.getList1());
			}
		
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void test2() {
		TestBean bean = new TestBean();
		AccessorMethodSetter setter = new AccessorMethodSetter(TestBean.class, bean, "list1");
		try {
			List list = new ArrayList();
			setter.set(list);
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
