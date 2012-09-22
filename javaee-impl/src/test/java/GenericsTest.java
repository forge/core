import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class GenericsTest extends AbstractShellTest
{

   @Test
   public void simple1() throws ClassNotFoundException
   {
      JavaClass c1 = JavaParser.parse(JavaClass.class, "package it.coopservice.test; public class Bar<T> {}");
      JavaClass javaClass = JavaParser.create(JavaClass.class);
      javaClass.setName("TBarTest");
      javaClass.setSuperType(c1);
      javaClass.setPackage("it.coopservice.test");
      javaClass.addImport("it.coopservice.test.Bar");
      String content = javaClass.toString();
      System.out.println(content);
      assertTrue(content.contains("<T>"));
   }

   @Test
   public void simple2() throws ClassNotFoundException
   {
      JavaClass c0 = JavaParser.parse(JavaClass.class, "package it.coopservice.test; public class Foo {}");
      JavaClass c1 = JavaParser.parse(JavaClass.class, "package it.coopservice.test; public class Bar<Foo> {}");
      JavaClass javaClass = JavaParser.create(JavaClass.class);
      javaClass.setName("FooBarTest");
      javaClass.setSuperType(c1);
      javaClass.setPackage("it.coopservice.test");
      javaClass.addImport("it.coopservice.test.Bar");
      javaClass.addImport("it.coopservice.test.Foo");
      String content = javaClass.toString();
      System.out.println(content);
      assertTrue(content.contains("Bar<Foo>"));
   }

   @Test
   public void simple3() throws ClassNotFoundException
   {
      JavaClass c0 = JavaParser.parse(JavaClass.class, "package it.coopservice.test; public class Foo {}");
      JavaClass c1 = JavaParser.parse(JavaClass.class, "package it.coopservice.test; public class Bar<Foo> {}");
      JavaClass javaClass = JavaParser.create(JavaClass.class);
      javaClass.setName("FooBarDirectTest");
      javaClass.setPackage("it.coopservice.test");
      javaClass.addImport("it.coopservice.test.Bar");
      javaClass.addImport("it.coopservice.test.Foo");
      javaClass.setSuperType("Bar<Foo>");
      String content = javaClass.toString();
      System.out.println(content);
      assertTrue(content.contains("Bar<Foo>"));
   }

}
