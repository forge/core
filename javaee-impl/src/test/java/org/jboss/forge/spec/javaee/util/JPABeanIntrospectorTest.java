package org.jboss.forge.spec.javaee.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItem;

import javax.persistence.Id;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.junit.Test;

public class JPABeanIntrospectorTest
{
   @Test
   public void testGetFieldAsProperty() throws Exception
   {
      JavaClass entity = JavaParser.parse(JavaClass.class,
               "import javax.persistence.Id; " +
                        "public class Test{ " +
                        "  @Id private Long id;" +
                        "}");
      JPABeanIntrospector bean = new JPABeanIntrospector(entity);
      assertThat(bean.getProperties(), hasItem(new JPAProperty("id")));
      assertThat(bean.getProperties().get(0).isReadable(), equalTo(false));
      assertThat(bean.getProperties().get(0).isWritable(), equalTo(false));
      assertThat(bean.getProperties().get(0).hasAnnotation(Id.class), equalTo(true));
      assertThat(bean.getProperties().get(0).isPrimitive(), equalTo(false));
   }

   @Test
   public void testGetMethodAsProperty() throws Exception
   {
      JavaClass entity = JavaParser.parse(JavaClass.class,
               "import javax.persistence.Id; " +
                        "public class Test{" +
                        "  @Id" +
                        "  public Long getId() {" +
                        "  } " +
                        "}");
      JPABeanIntrospector bean = new JPABeanIntrospector(entity);
      assertThat(bean.getProperties(), hasItem(new JPAProperty("id")));
      assertThat(bean.getProperties().get(0).isReadable(), equalTo(true));
      assertThat(bean.getProperties().get(0).isWritable(), equalTo(false));
      assertThat(bean.getProperties().get(0).isReadOnly(), equalTo(true));
      assertThat(bean.getProperties().get(0).hasAnnotation(Id.class), equalTo(true));
      assertThat(bean.getProperties().get(0).isPrimitive(), equalTo(false));
   }
   
   @Test
   public void testGetBooleanFieldAsProperty() throws Exception
   {
      JavaClass entity = JavaParser.parse(JavaClass.class,
               "import javax.persistence.Id; " +
                        "public class Test{ " +
                        "  private Boolean flag;" +
                        "}");
      JPABeanIntrospector bean = new JPABeanIntrospector(entity);
      assertThat(bean.getProperties(), hasItem(new JPAProperty("flag")));
      assertThat(bean.getProperties().get(0).isReadable(), equalTo(false));
      assertThat(bean.getProperties().get(0).isWritable(), equalTo(false));
      assertThat(bean.getProperties().get(0).isPrimitive(), equalTo(false));
   }

   @Test
   public void testGetMethodWithBooleanReturnAsProperty() throws Exception
   {
      JavaClass entity = JavaParser.parse(JavaClass.class,
               "import javax.persistence.Id; " +
                        "public class Test{" +
                        "  public Boolean isFlag() {" +
                        "  } " +
                        "}");
      JPABeanIntrospector bean = new JPABeanIntrospector(entity);
      assertThat(bean.getProperties(), hasItem(new JPAProperty("flag")));
      assertThat(bean.getProperties().get(0).isReadable(), equalTo(true));
      assertThat(bean.getProperties().get(0).isWritable(), equalTo(false));
      assertThat(bean.getProperties().get(0).isReadOnly(), equalTo(true));
      assertThat(bean.getProperties().get(0).isPrimitive(), equalTo(false));
   }
   
   @Test
   public void testGetPrimitiveBooleanFieldAsProperty() throws Exception
   {
      JavaClass entity = JavaParser.parse(JavaClass.class,
               "import javax.persistence.Id; " +
                        "public class Test{ " +
                        "  private boolean flag;" +
                        "}");
      JPABeanIntrospector bean = new JPABeanIntrospector(entity);
      assertThat(bean.getProperties(), hasItem(new JPAProperty("flag")));
      assertThat(bean.getProperties().get(0).isReadable(), equalTo(false));
      assertThat(bean.getProperties().get(0).isWritable(), equalTo(false));
      assertThat(bean.getProperties().get(0).isPrimitive(), equalTo(true));
   }

   @Test
   public void testStaticFieldAsProperty() throws Exception
   {
      JavaClass entity = JavaParser.parse(JavaClass.class,
               "import javax.persistence.Id; " +
                        "public class Test{" +
                        "  public static boolean isFlag;" +
                        "}");
      JPABeanIntrospector bean = new JPABeanIntrospector(entity);
      assertThat(bean.getProperties().size(), equalTo(0));
   }
   
   @Test
   public void testStaticMethodAsProperty() throws Exception
   {
      JavaClass entity = JavaParser.parse(JavaClass.class,
               "import javax.persistence.Id; " +
                        "public class Test{" +
                        "  public static Boolean isFlag() {" +
                        "  } " +
                        "}");
      JPABeanIntrospector bean = new JPABeanIntrospector(entity);
      assertThat(bean.getProperties().size(), equalTo(0));
   }
}
