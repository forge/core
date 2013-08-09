package org.jboss.forge.spec.javaee.util;

import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItem;

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
   }
}
