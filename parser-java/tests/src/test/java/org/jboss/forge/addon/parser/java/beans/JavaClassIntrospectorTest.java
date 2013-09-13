/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.beans;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.junit.Test;

public class JavaClassIntrospectorTest
{
   @Test
   public void testStaticFieldAsProperty() throws Exception
   {
      JavaClass entity = JavaParser.parse(JavaClass.class,
               "import javax.persistence.Id; " +
                        "public class Test{" +
                        "  public static boolean isFlag;" +
                        "}");
      JavaClassIntrospector introspector = new JavaClassIntrospector(entity);
      assertThat(introspector.getProperties().size(), equalTo(0));
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
      JavaClassIntrospector introspector = new JavaClassIntrospector(entity);
      assertThat(introspector.getProperties().size(), equalTo(0));
   }

   @Test
   public void testGetFieldAsProperty() throws Exception
   {
      JavaClass entity = JavaParser.parse(JavaClass.class,
               "import javax.persistence.Id; " +
                        "public class Test{ " +
                        "  @Deprecated private Long id;" +
                        "}");
      JavaClassIntrospector introspector = new JavaClassIntrospector(entity);
      assertThat(introspector.getProperties().size(), equalTo(0));
   }

   @Test
   public void testGetBooleanFieldAsProperty() throws Exception
   {
      JavaClass entity = JavaParser.parse(JavaClass.class,
               "import javax.persistence.Id; " +
                        "public class Test{ " +
                        "  private Boolean flag;" +
                        "}");
      JavaClassIntrospector introspector = new JavaClassIntrospector(entity);
      assertThat(introspector.getProperties().size(), equalTo(0));
   }

   @Test
   public void testGetPrimitiveBooleanFieldAsProperty() throws Exception
   {
      JavaClass entity = JavaParser.parse(JavaClass.class,
               "import javax.persistence.Id; " +
                        "public class Test{ " +
                        "  private boolean flag;" +
                        "}");
      JavaClassIntrospector introspector = new JavaClassIntrospector(entity);
      assertThat(introspector.getProperties().size(), equalTo(0));
   }

   @Test
   public void testGetAccessorAsProperty() throws Exception
   {
      JavaClass entity = JavaParser.parse(JavaClass.class,
               "import javax.persistence.Id; " +
                        "public class Test{" +
                        "  @Deprecated" +
                        "  public Long getId() {" +
                        "  } " +
                        "}");
      JavaClassIntrospector introspector = new JavaClassIntrospector(entity);
      assertThat(introspector.getProperties(), hasItem(new Property("id")));
      assertThat(introspector.getProperties().get(0).isReadable(), equalTo(true));
      assertThat(introspector.getProperties().get(0).isWritable(), equalTo(false));
      assertThat(introspector.getProperties().get(0).isReadOnly(), equalTo(true));
      assertThat(introspector.getProperties().get(0).isWriteOnly(), equalTo(false));
      assertThat(introspector.getProperties().get(0).hasAnnotation(Deprecated.class), equalTo(true));
      assertThat(introspector.getProperties().get(0).isPrimitive(), equalTo(false));
   }

   @Test
   public void testGetBooleanAccessorAsProperty() throws Exception
   {
      JavaClass entity = JavaParser.parse(JavaClass.class,
               "import javax.persistence.Id; " +
                        "public class Test{" +
                        "  public Boolean isFlag() {" +
                        "  } " +
                        "}");
      JavaClassIntrospector introspector = new JavaClassIntrospector(entity);
      assertThat(introspector.getProperties(), hasItem(new Property("flag")));
      assertThat(introspector.getProperties().get(0).isReadable(), equalTo(true));
      assertThat(introspector.getProperties().get(0).isWritable(), equalTo(false));
      assertThat(introspector.getProperties().get(0).isReadOnly(), equalTo(true));
      assertThat(introspector.getProperties().get(0).isWriteOnly(), equalTo(false));
      assertThat(introspector.getProperties().get(0).isPrimitive(), equalTo(false));
   }

   @Test
   public void testGetMutatorAsProperty() throws Exception
   {
      JavaClass entity = JavaParser.parse(JavaClass.class,
               "import javax.persistence.Id; " +
                        "public class Test{" +
                        "  public void setFlag(Boolean flag) {" +
                        "   " +
                        "  }" +
                        "}");
      JavaClassIntrospector introspector = new JavaClassIntrospector(entity);
      assertThat(introspector.getProperties(), hasItem(new Property("flag")));
      assertThat(introspector.getProperties().get(0).isReadable(), equalTo(false));
      assertThat(introspector.getProperties().get(0).isWritable(), equalTo(true));
      assertThat(introspector.getProperties().get(0).isReadOnly(), equalTo(false));
      assertThat(introspector.getProperties().get(0).isWriteOnly(), equalTo(true));
      assertThat(introspector.getProperties().get(0).isPrimitive(), equalTo(false));
   }

   @Test
   public void testGetAccessorAndMutatorAsProperty() throws Exception
   {
      JavaClass entity = JavaParser.parse(JavaClass.class,
               "import javax.persistence.Id; " +
                        "public class Test{" +
                        "  public Boolean isFlag() {" +
                        "    " +
                        "  } " +
                        "    " +
                        "  public void setFlag(Boolean flag) {" +
                        "    " +
                        "  } " +
                        "}");
      JavaClassIntrospector introspector = new JavaClassIntrospector(entity);
      assertThat(introspector.getProperties(), hasItem(new Property("flag")));
      assertThat(introspector.getProperties().get(0).isReadable(), equalTo(true));
      assertThat(introspector.getProperties().get(0).isWritable(), equalTo(true));
      assertThat(introspector.getProperties().get(0).isReadOnly(), equalTo(false));
      assertThat(introspector.getProperties().get(0).isWriteOnly(), equalTo(false));
      assertThat(introspector.getProperties().get(0).isPrimitive(), equalTo(false));
   }
}
