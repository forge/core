/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.test.parser.java.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.AnnotationTarget;
import org.jboss.forge.parser.java.JavaSource;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class AnnotationTest<O extends JavaSource<O>, T>
{
   private AnnotationTarget<O, T> target;

   protected AnnotationTarget<O, T> getTarget()
   {
      return target;
   }

   protected void setTarget(final AnnotationTarget<O, T> target)
   {
      this.target = target;
   }

   @Before
   public void reset()
   {
      resetTests();
   }

   public abstract void resetTests();

   @Test
   public void testParseAnnotation() throws Exception
   {
      List<Annotation<O>> annotations = target.getAnnotations();
      assertEquals(4, annotations.size());
      assertEquals("deprecation", annotations.get(1).getStringValue());
      assertEquals("deprecation", annotations.get(1).getStringValue("value"));
      assertEquals("value", annotations.get(1).getValues().get(0).getName());
      assertEquals("deprecation", annotations.get(1).getValues().get(0).getStringValue());

      assertEquals("unchecked", annotations.get(2).getStringValue("value"));
      assertEquals("unchecked", annotations.get(2).getStringValue());
      assertEquals("value", annotations.get(2).getValues().get(0).getName());
      assertEquals("unchecked", annotations.get(2).getValues().get(0).getStringValue());
   }

   @Test
   public void testAddAnnotation() throws Exception
   {
      int size = target.getAnnotations().size();
      Annotation<O> annotation = target.addAnnotation().setName("RequestScoped");
      List<Annotation<O>> annotations = target.getAnnotations();
      assertEquals(size + 1, annotations.size());
      assertEquals("RequestScoped", annotation.getName());
   }

   @Test
   public void testAddAnnotationByClass() throws Exception
   {
      int size = target.getAnnotations().size();
      Annotation<O> annotation = target.addAnnotation(Test.class);
      List<Annotation<O>> annotations = target.getAnnotations();
      assertEquals(size + 1, annotations.size());
      assertEquals(Test.class.getSimpleName(), annotation.getName());
      assertTrue(target.toString().contains("@" + Test.class.getSimpleName()));
      assertTrue(target.getOrigin().hasImport(Test.class));
   }

   @Test
   public void testAddAnnotationByName() throws Exception
   {
      int size = target.getAnnotations().size();
      Annotation<O> annotation = target.addAnnotation("RequestScoped");
      List<Annotation<O>> annotations = target.getAnnotations();
      assertEquals(size + 1, annotations.size());
      assertEquals("RequestScoped", annotation.getName());
      assertTrue(target.toString().contains("@RequestScoped"));
      assertFalse(target.getOrigin().hasImport("RequestScoped"));
   }

   @Test
   public void testCanAddAnnotationDuplicate() throws Exception
   {
      int size = target.getAnnotations().size();
      Annotation<O> anno1 = target.addAnnotation(Test.class);
      Annotation<O> anno2 = target.addAnnotation(Test.class);
      List<Annotation<O>> annotations = target.getAnnotations();
      assertEquals(size + 2, annotations.size());
      assertEquals(Test.class.getSimpleName(), anno1.getName());
      assertEquals(Test.class.getSimpleName(), anno2.getName());
      Pattern pattern = Pattern.compile("@" + Test.class.getSimpleName() + "\\s*" + "@" + Test.class.getSimpleName());
      Matcher matcher = pattern.matcher(target.toString());
      assertTrue(matcher.find());
      assertTrue(target.getOrigin().hasImport(Test.class));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testCannotAddAnnotationWithIllegalName() throws Exception
   {
      target.addAnnotation("sdf*(&#$%");
   }

   @Test
   public void testAddEnumValue() throws Exception
   {
      target.addAnnotation(Test.class).setEnumValue(MockEnumType.FOO);

      List<Annotation<O>> annotations = target.getAnnotations();

      Annotation<O> annotation = annotations.get(annotations.size() - 1);
      MockEnumType enumValue = annotation.getEnumValue(MockEnumType.class);
      assertEquals(MockEnumType.FOO, enumValue);
   }

   @Test
   public void testAddEnumNameValue() throws Exception
   {
      target.addAnnotation(Test.class).setEnumValue("name", MockEnumType.BAR);

      List<Annotation<O>> annotations = target.getAnnotations();

      Annotation<O> annotation = annotations.get(annotations.size() - 1);
      MockEnumType enumValue = annotation.getEnumValue(MockEnumType.class, "name");
      assertEquals(MockEnumType.BAR, enumValue);
   }

   @Test
   public void testAddLiteralValue() throws Exception
   {
      int size = target.getAnnotations().size();

      target.addAnnotation(Test.class).setLiteralValue("435");

      List<Annotation<O>> annotations = target.getAnnotations();
      assertEquals(size + 1, annotations.size());

      Annotation<O> annotation = annotations.get(annotations.size() - 1);
      assertEquals(Test.class.getSimpleName(), annotation.getName());
      assertEquals("435", annotation.getLiteralValue());
   }

   @Test
   public void testAddObjectValue() throws Exception
   {
      int size = target.getAnnotations().size();

      target.addAnnotation(Test.class).setLiteralValue("expected", "RuntimeException.class")
               .setLiteralValue("foo", "bar");

      List<Annotation<O>> annotations = target.getAnnotations();
      assertEquals(size + 1, annotations.size());

      Annotation<O> annotation = annotations.get(annotations.size() - 1);
      assertEquals(Test.class.getSimpleName(), annotation.getName());
      assertEquals(null, annotation.getLiteralValue());
      assertEquals("RuntimeException.class", annotation.getLiteralValue("expected"));
      assertEquals("bar", annotation.getLiteralValue("foo"));
   }

   @Test
   public void testAddValueConvertsToNormalAnnotation() throws Exception
   {
      target.addAnnotation(Test.class).setLiteralValue("RuntimeException.class");
      Annotation<O> annotation = target.getAnnotations().get(target.getAnnotations().size() - 1);

      assertEquals("RuntimeException.class", annotation.getLiteralValue());
      assertTrue(annotation.isSingleValue());

      annotation.setLiteralValue("foo", "bar");
      assertFalse(annotation.isSingleValue());
      assertTrue(annotation.isNormal());

      assertEquals("RuntimeException.class", annotation.getLiteralValue());
      assertEquals("RuntimeException.class", annotation.getLiteralValue("value"));
      assertEquals("bar", annotation.getLiteralValue("foo"));
   }

   @Test
   public void testAnnotationBeginsAsMarker() throws Exception
   {
      Annotation<O> anno = target.addAnnotation(Test.class);
      assertTrue(anno.isMarker());
      assertFalse(anno.isSingleValue());
      assertFalse(anno.isNormal());

      anno.setLiteralValue("\"Foo!\"");
      assertFalse(anno.isMarker());
      assertTrue(anno.isSingleValue());
      assertFalse(anno.isNormal());

      anno.setStringValue("bar", "Foo!");
      assertFalse(anno.isMarker());
      assertFalse(anno.isSingleValue());
      assertTrue(anno.isNormal());

      assertEquals("\"Foo!\"", anno.getLiteralValue("bar"));
      assertEquals("Foo!", anno.getStringValue("bar"));

      anno.removeAllValues();
      assertTrue(anno.isMarker());
      assertFalse(anno.isSingleValue());
      assertFalse(anno.isNormal());
   }

   @Test
   public void testHasAnnotationClassType() throws Exception
   {
      target.addAnnotation(Test.class);
      assertTrue(target.hasAnnotation(Test.class));
   }

   @Test
   public void testHasAnnotationStringType() throws Exception
   {
      target.addAnnotation(Test.class);
      assertTrue(target.hasAnnotation("Test"));
      assertTrue(target.hasAnnotation(Test.class.getName()));
   }

   @Test
   public void testHasAnnotationStringTypeSimple() throws Exception
   {
      target.addAnnotation(Test.class);
      assertNotNull(target.getAnnotation("Test"));
      assertNotNull(target.getAnnotation(Test.class.getSimpleName()));
   }

   @Test
   public void testGetAnnotationClassType() throws Exception
   {
      target.addAnnotation(Test.class);
      assertNotNull(target.getAnnotation(Test.class));
   }

   @Test
   public void testGetAnnotationStringType() throws Exception
   {
      target.addAnnotation(Test.class);
      assertNotNull(target.getAnnotation("org.junit.Test"));
      assertNotNull(target.getAnnotation(Test.class.getName()));
   }

   @Test
   public void testGetAnnotationStringTypeSimple() throws Exception
   {
      target.addAnnotation(Test.class);
      assertTrue(target.hasAnnotation("Test"));
      assertTrue(target.hasAnnotation(Test.class.getSimpleName()));
   }

   @Test
   public void testRemoveAllValues() throws Exception
   {
      target.addAnnotation(Test.class).setLiteralValue("expected", "RuntimeException.class");

      List<Annotation<O>> annotations = target.getAnnotations();
      Annotation<O> annotation = annotations.get(annotations.size() - 1);
      annotation.removeAllValues();

      assertEquals(0, annotation.getValues().size());
   }

   @Test
   public void testGetNames() throws Exception
   {
      target.addAnnotation(Test.class).setLiteralValue("expected", "RuntimeException.class");

      Annotation<O> annotation = target.getAnnotation(Test.class);
      assertEquals(Test.class.getSimpleName(), annotation.getName());
      assertEquals(Test.class.getName(), annotation.getQualifiedName());
   }
}
