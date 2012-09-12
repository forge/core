package org.jboss.forge.spec.ejb;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.spec.javaee.ejb.api.EjbType;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class EjbInitialTests extends AbstractShellTest {

	@Test
	public void testAddMethod() throws ClassNotFoundException {
		JavaClass javaClass = JavaParser.create(JavaClass.class);
		javaClass.setName("FlowerTest");
		javaClass.setPackage("it.coopservice.test");
		javaClass.addInterface("org.jboss.forge.spec.javaee.ejb.TestEjb");
		javaClass.addMethod().setPublic().setName("hello")
				.setReturnType("java.lang.String").setParameters("String name")
				.setBody("return null;");

		javaClass.addMethod().setPublic().setName("hello")
				.setReturnType("void").setBody("");
		String content = javaClass.toString();
		Assert.assertNotNull(content);
	}

	@Test
	public void testAddMethodsFromInteface() {
		JavaClass javaClass = JavaParser.create(JavaClass.class);
		javaClass.setName("FlowerTest");
		javaClass.setPackage("it.coopservice.test");
		javaClass.addInterface(Serializable.class);
		Class clazz = null;
		try {
			clazz = Class.forName(Serializable.class.getName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			org.jboss.forge.parser.java.Method<JavaClass> methodJavaClass = javaClass
					.addMethod();
			if (!method.getReturnType().isPrimitive()) {
				javaClass.addImport(method.getReturnType());
				methodJavaClass.setBody(" return null;");
			} else {
				methodJavaClass.setBody("");
			}
			methodJavaClass.setPublic().setReturnType(method.getReturnType())
					.setName(method.getName());
			Class<?>[] params = method.getParameterTypes();
			if (params != null && params.length > 0) {
				int i = 0;
				StringBuffer sb = new StringBuffer();
				for (Class<?> class1 : params) {
					sb.append("," + class1.getName() + " arg" + i);
					i++;
					if (!javaClass.getInterfaces().contains(class1)) {
						javaClass.addImport(class1);
						// System.out.println(i + ") " + class1);
					} else {
						// System.out.println("NO) " + class1);
					}
				}
				methodJavaClass.setParameters(sb.toString().substring(1));
			}
		}
		String content = javaClass.toString();
		Assert.assertNotNull(content);
	}

	@Test
	public void testReadMDBAnnotations() {
		String javaMdb = "package it.coopservice.posteit.jms;"
				+ "import javax.ejb.ActivationConfigProperty;"
				+ "import javax.ejb.MessageDriven;"
				+ "import javax.inject.Inject;"
				+ "import javax.jms.MapMessage;"
				+ "import javax.jms.Message;"
				+ "import javax.jms.MessageListener;"
				+ "@MessageDriven(name = \"CloseDispatchToPosteItMDB\", activationConfig = {"
				+ "		@ActivationConfigProperty(propertyName = \"destinationType\", propertyValue = \"javax.jms.Queue\"),"
				+ "@ActivationConfigProperty(propertyName = \"destination\", propertyValue = \"/queue/test\"),"
				+ "		@ActivationConfigProperty(propertyName = \"acknowledgeMode\", propertyValue = \"Auto-acknowledge\"),"
				+ "@ActivationConfigProperty(propertyName = \"maxSession\", propertyValue = \"5\"),"
				+ "		@ActivationConfigProperty(propertyName = \"transactionTimeout\", propertyValue = \"3600\"),"
				+ "@ActivationConfigProperty(propertyName = \"dLQMaxResent\", propertyValue = \"0\") })"
				+ "public class CloseDispatchToPosteItMDB implements MessageListener {"
				+ "public void onMessage(Message message) {"

				+ "}" + "}";
		JavaClass javaClass = (JavaClass) JavaParser.parse(javaMdb);
		List<Annotation<JavaClass>> lista = javaClass.getAnnotations();
		Assert.assertNotNull(lista);
	}

	@Test
	public void testAddAnnotationForMDB_NO() {
		JavaClass javaClass = JavaParser.create(JavaClass.class);
		javaClass.setName("FlowerTest");
		javaClass.setPackage("it.coopservice.test");
		javaClass.addImport(ActivationConfigProperty.class);
		javaClass.addImport(MessageDriven.class);
		javaClass.addInterface(MessageListener.class);
		javaClass.addMethod("public void onMessage(Message message) {}");
		javaClass
				.addAnnotation(EjbType.MESSAGEDRIVEN.getAnnotation())
				// .setLiteralValue("name", "testName");
				.setLiteralValue(
						" @MessageDriven(name = \"CloseDispatchToPosteItMDB\", activationConfig = {"
								+ "		@ActivationConfigProperty(propertyName = \"destinationType\", propertyValue = \"javax.jms.Queue\"),"
								+ "@ActivationConfigProperty(propertyName = \"destination\", propertyValue = \"/queue/test\") })");

		String content = javaClass.toString();
		Assert.assertTrue(content.contains("@MessageDriven"));
	}

	@Test
	public void testAddAnnotationForMDB_OK() {
		JavaClass javaClass = JavaParser.create(JavaClass.class);
		javaClass.setName("FlowerTest");
		javaClass.setPackage("it.coopservice.test");
		javaClass.addImport(ActivationConfigProperty.class);
		javaClass.addImport(MessageDriven.class);
		javaClass.addImport(Message.class);
		javaClass.addInterface(MessageListener.class);
		javaClass.addMethod("public void onMessage(Message message) {}");
		javaClass.addAnnotation(EjbType.MESSAGEDRIVEN.getAnnotation())
				// .setLiteralValue("name", "testName");
				.setLiteralValue("name", "\"CloseDispatchToPosteItMDB\"")
				.setLiteralValue(
						"activationConfig",
						"{@ActivationConfigProperty(propertyName = \"destinationType\", propertyValue = \"javax.jms.Queue\"), "
								+ "@ActivationConfigProperty(propertyName = \"destination\", propertyValue = \"/queue/test\")"
								+ "}");

		String content = javaClass.toString();
		Assert.assertTrue(content.contains("@MessageDriven"));
	}

}
