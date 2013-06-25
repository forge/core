/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.faces.metawidget.widgetbuilder;

import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.INVERSE_FIELD;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_MANY_TO_MANY;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_MANY_TO_ONE;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_ONE_TO_MANY;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_ONE_TO_ONE;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_REL_TYPE;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.N_TO_MANY;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.ONE_TO_ONE;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.OWNING_FIELD;
import static org.junit.Assert.*;
import static org.metawidget.inspector.InspectionResultConstants.NAME;
import static org.metawidget.inspector.InspectionResultConstants.PARAMETERIZED_TYPE;
import static org.metawidget.inspector.InspectionResultConstants.PROPERTY;
import static org.metawidget.inspector.InspectionResultConstants.READ_ONLY;
import static org.metawidget.inspector.InspectionResultConstants.TRUE;
import static org.metawidget.inspector.InspectionResultConstants.TYPE;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.FACES_LOOKUP;

import java.util.Map;

import org.junit.Test;
import org.metawidget.statically.javacode.StaticJavaMetawidget;
import org.metawidget.util.CollectionUtils;

public class RemoveEntityWidgetBuilderTest
{

   @Test
   public void testRemoveEntityNoRelation() throws Exception
   {
      RemoveEntityWidgetBuilder widgetBuilder = new RemoveEntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, "com.acme.Example");
      attributes.put(READ_ONLY, TRUE);
      attributes.put(FACES_LOOKUP, "#{barBean.all}");
      assertEquals("", widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget()).toString());
   }
   
   @Test
   public void testRemoveEntityUniOneToOneRelation() throws Exception
   {
      RemoveEntityWidgetBuilder widgetBuilder = new RemoveEntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "foo");
      attributes.put(TYPE, "com.acme.Example");
      attributes.put(ONE_TO_ONE, TRUE);
      attributes.put(JPA_REL_TYPE, JPA_ONE_TO_ONE);
      assertEquals("", widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget()).toString());
   }
   
   @Test
   public void testRemoveEntityBidiOneToOneRelationOwningSide() throws Exception
   {
      RemoveEntityWidgetBuilder widgetBuilder = new RemoveEntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "foo");
      attributes.put(TYPE, "com.acme.Example");
      attributes.put(ONE_TO_ONE, TRUE);
      attributes.put(JPA_REL_TYPE, JPA_ONE_TO_ONE);
      attributes.put(OWNING_FIELD, "foo");
      attributes.put(INVERSE_FIELD, "bar");
      assertEquals(
               "Example foo = deletableEntity.getFoo();deletableEntity.setBar(null);this.entityManager.merge(foo);",
               widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget()).toString());
   }

   @Test
   public void testRemoveEntityBidiOneToOneRelationInverseSide() throws Exception
   {
      RemoveEntityWidgetBuilder widgetBuilder = new RemoveEntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, "com.acme.Example");
      attributes.put(ONE_TO_ONE, TRUE);
      attributes.put(JPA_REL_TYPE, JPA_ONE_TO_ONE);
      attributes.put(OWNING_FIELD, "foo");
      attributes.put(INVERSE_FIELD, "bar");
      assertEquals(
               "Example bar = deletableEntity.getBar();deletableEntity.setFoo(null);this.entityManager.merge(bar);",
               widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget()).toString());
   }
   
   @Test
   public void testRemoveEntityUniOneToManyRelation() throws Exception
   {
      RemoveEntityWidgetBuilder widgetBuilder = new RemoveEntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, "java.util.Set");
      attributes.put(PARAMETERIZED_TYPE, "com.acme.Example");
      attributes.put(N_TO_MANY, TRUE);
      attributes.put(JPA_REL_TYPE, JPA_ONE_TO_MANY);
      assertEquals("", widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget()).toString());
   }

   @Test
   public void testRemoveEntityBidiOneToManyRelationInverseSide() throws Exception
   {
      RemoveEntityWidgetBuilder widgetBuilder = new RemoveEntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, "java.util.Set");
      attributes.put(PARAMETERIZED_TYPE, "com.acme.Example");
      attributes.put(N_TO_MANY, TRUE);
      attributes.put(JPA_REL_TYPE, JPA_ONE_TO_MANY);
      attributes.put(OWNING_FIELD, "foo");
      attributes.put(INVERSE_FIELD, "bar");
      assertEquals(
               "Iterator<Example> iterBar = deletableEntity.getBar().iterator();for (; iterBar.hasNext() ;)  { Example example = iterBar.next();example.setFoo(null);iterBar.remove();this.entityManager.merge(example); }",
               widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget()).toString());
   }

   @Test
   public void testRemoveEntityUniManyToOneRelation() throws Exception
   {
      RemoveEntityWidgetBuilder widgetBuilder = new RemoveEntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, "com.acme.Example");
      attributes.put(JPA_REL_TYPE, JPA_MANY_TO_ONE);
      assertEquals("", widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget()).toString());
   }
   
   @Test
   public void testRemoveEntityBidiManyToOneRelationOwningSide() throws Exception
   {
      RemoveEntityWidgetBuilder widgetBuilder = new RemoveEntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, "com.acme.Example");
      attributes.put(JPA_REL_TYPE, JPA_MANY_TO_ONE);
      attributes.put(OWNING_FIELD, "bar");
      attributes.put(INVERSE_FIELD, "foo");
      assertEquals(
               "Example bar = deletableEntity.getBar();bar.getFoo().remove(deletableEntity);deletableEntity.setBar(null);this.entityManager.merge(bar);",
               widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget()).toString());
   }
   
   @Test
   public void testRemoveEntityUniManyToManyRelation() throws Exception
   {
      RemoveEntityWidgetBuilder widgetBuilder = new RemoveEntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, "java.util.Set");
      attributes.put(PARAMETERIZED_TYPE, "com.acme.Example");
      attributes.put(N_TO_MANY, TRUE);
      attributes.put(JPA_REL_TYPE, JPA_MANY_TO_MANY);
      assertEquals("", widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget()).toString());
   }

   @Test
   public void testRemoveEntityBidiManyToManyRelationInverseSide() throws Exception
   {
      RemoveEntityWidgetBuilder widgetBuilder = new RemoveEntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, "java.util.Set");
      attributes.put(PARAMETERIZED_TYPE, "com.acme.Example");
      attributes.put(N_TO_MANY, TRUE);
      attributes.put(JPA_REL_TYPE, JPA_MANY_TO_MANY);
      attributes.put(OWNING_FIELD, "foo");
      attributes.put(INVERSE_FIELD, "bar");
      assertEquals(
               "Iterator<Example> iterBar = deletableEntity.getBar().iterator();for (; iterBar.hasNext() ;)  { Example example = iterBar.next();example.getFoo().remove(deletableEntity);iterBar.remove();this.entityManager.merge(example); }",
               widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget()).toString());
   }

   @Test
   public void testRemoveEntityBidiManyToManyRelationOwningSide() throws Exception
   {
      RemoveEntityWidgetBuilder widgetBuilder = new RemoveEntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "foo");
      attributes.put(TYPE, "java.util.Set");
      attributes.put(PARAMETERIZED_TYPE, "com.acme.Example");
      attributes.put(N_TO_MANY, TRUE);
      attributes.put(JPA_REL_TYPE, JPA_MANY_TO_MANY);
      attributes.put(OWNING_FIELD, "foo");
      attributes.put(INVERSE_FIELD, "bar");
      assertEquals(
               "Iterator<Example> iterFoo = deletableEntity.getFoo().iterator();for (; iterFoo.hasNext() ;)  { Example example = iterFoo.next();example.getBar().remove(deletableEntity);iterFoo.remove();this.entityManager.merge(example); }",
               widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget()).toString());
   }
}