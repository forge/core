/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.faces.metawidget.inspector;

import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.INVERSE_FIELD;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_MANY_TO_MANY;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_MANY_TO_ONE;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_ONE_TO_MANY;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_ONE_TO_ONE;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_REL_TYPE;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.N_TO_MANY;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.ONE_TO_ONE;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.OWNING_FIELD;
import static org.metawidget.inspector.InspectionResultConstants.ENTITY;
import static org.metawidget.inspector.InspectionResultConstants.NAME;
import static org.metawidget.inspector.InspectionResultConstants.PROPERTY;
import static org.metawidget.inspector.InspectionResultConstants.TRUE;
import static org.metawidget.inspector.InspectionResultConstants.TYPE;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.FACES_CONVERTER_ID;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.FACES_LOOKUP;

import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import junit.framework.TestCase;

import org.metawidget.inspector.impl.BaseObjectInspectorConfig;
import org.metawidget.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ForgeInspectorTest
         extends TestCase
{
   //
   // Public methods
   //

   public void testRelationships()
   {
      String xml = new ForgeInspector(new BaseObjectInspectorConfig()).inspect(new Foo(), Foo.class.getName());
      Document document = XmlUtils.documentFromString(xml);
      assertEquals("inspection-result", document.getFirstChild().getNodeName());
      Element entity = (Element) document.getFirstChild().getFirstChild();
      assertEquals(ENTITY, entity.getNodeName());
      assertEquals(Foo.class.getName(), entity.getAttribute(TYPE));

      Element property = (Element) entity.getFirstChild();
      assertEquals(PROPERTY, property.getNodeName());
      assertEquals("embedded", property.getAttribute(NAME));
      assertEquals(TRUE, property.getAttribute(ONE_TO_ONE));
      assertEquals(JPA_ONE_TO_ONE, property.getAttribute(JPA_REL_TYPE));
      assertEquals(3, property.getAttributes().getLength());

      property = XmlUtils.getNextSiblingElement(property);
      assertEquals(PROPERTY, property.getNodeName());
      assertEquals("manyToMany", property.getAttribute(NAME));
      assertEquals(TRUE, property.getAttribute(N_TO_MANY));
      assertEquals(JPA_MANY_TO_MANY, property.getAttribute(JPA_REL_TYPE));
      assertEquals(3, property.getAttributes().getLength());

      property = XmlUtils.getNextSiblingElement(property);
      assertEquals(PROPERTY, property.getNodeName());
      assertEquals("manyToOne", property.getAttribute(NAME));
      assertEquals("#{forgeInspectorTest$BarBean.all}", property.getAttribute(FACES_LOOKUP));
      assertEquals("#{forgeInspectorTest$BarBean.converter}", property.getAttribute(FACES_CONVERTER_ID));
      assertEquals(JPA_MANY_TO_ONE, property.getAttribute(JPA_REL_TYPE));
      assertEquals(5, property.getAttributes().getLength());

      property = XmlUtils.getNextSiblingElement(property);
      assertEquals(PROPERTY, property.getNodeName());
      assertEquals("manyToOneBidi", property.getAttribute(NAME));
      assertEquals("#{forgeInspectorTest$BarBean.all}", property.getAttribute(FACES_LOOKUP));
      assertEquals("#{forgeInspectorTest$BarBean.converter}", property.getAttribute(FACES_CONVERTER_ID));
      assertEquals("foos", property.getAttribute(INVERSE_FIELD));
      assertEquals("manyToOneBidi", property.getAttribute(OWNING_FIELD));
      assertEquals(JPA_MANY_TO_ONE, property.getAttribute(JPA_REL_TYPE));
      assertEquals(6, property.getAttributes().getLength());
      
      property = XmlUtils.getNextSiblingElement(property);
      assertEquals(PROPERTY, property.getNodeName());
      assertEquals("oneToMany", property.getAttribute(NAME));
      assertEquals(TRUE, property.getAttribute(N_TO_MANY));
      assertEquals("oneToMany", property.getAttribute(INVERSE_FIELD));
      assertEquals("bar", property.getAttribute(OWNING_FIELD));
      assertEquals(JPA_ONE_TO_MANY, property.getAttribute(JPA_REL_TYPE));
      assertEquals(5, property.getAttributes().getLength());

      property = XmlUtils.getNextSiblingElement(property);
      assertEquals(PROPERTY, property.getNodeName());
      assertEquals("oneToOne", property.getAttribute(NAME));
      assertEquals(TRUE, property.getAttribute(ONE_TO_ONE));
      assertEquals(JPA_ONE_TO_ONE, property.getAttribute(JPA_REL_TYPE));
      assertEquals(3, property.getAttributes().getLength());

      property = XmlUtils.getNextSiblingElement(property);
      assertEquals(PROPERTY, property.getNodeName());
      assertEquals("oneToOneMappedBy", property.getAttribute(NAME));
      assertEquals(TRUE, property.getAttribute(ONE_TO_ONE));
      assertEquals("oneToOneMappedBy", property.getAttribute(INVERSE_FIELD));
      assertEquals("foo", property.getAttribute(OWNING_FIELD));
      assertEquals(JPA_ONE_TO_ONE, property.getAttribute(JPA_REL_TYPE));
      assertEquals(5, property.getAttributes().getLength());

      assertEquals(7, entity.getChildNodes().getLength());
   }

   //
   // Inner class
   //

   static class Foo
   {
      @OneToOne(mappedBy = "foo")
      public Bar getOneToOneMappedBy()
      {
         return null;
      }

      @OneToOne
      public Bar getOneToOne()
      {
         return null;
      }

      @Embedded
      public Bar getEmbedded()
      {
         return null;
      }

      @OneToMany(mappedBy="bar")
      public Set<Bar> getOneToMany() {

         return null;
      }

      @ManyToOne
      public Bar getManyToOne() {

         return null;
      }
      
      @ManyToOne
      public Bar getManyToOneBidi() {

         return null;
      }

      @ManyToMany
      public Set<Bar> getManyToMany() {

         return null;
      }
   }

   static class Bar
   {
      public String getName() {

         return null;
      }
      
      @OneToOne()
      public Foo getFoo()
      {
         return null;
      }
      
      @ManyToOne()
      public Foo getBar()
      {
         return null;
      }
      
      @OneToMany(mappedBy="manyToOneBidi")
      public Set<Foo> getFoos() {

         return null;
      }
   }
}
