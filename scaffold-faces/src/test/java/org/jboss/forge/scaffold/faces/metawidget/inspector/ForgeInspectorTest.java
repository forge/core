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
package org.jboss.forge.scaffold.faces.metawidget.inspector;

import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.*;
import static org.metawidget.inspector.InspectionResultConstants.*;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.*;

import java.util.Set;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import junit.framework.TestCase;

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
      String xml = new ForgeInspector().inspect(new Foo(), Foo.class.getName());
      Document document = XmlUtils.documentFromString(xml);
      assertEquals("inspection-result", document.getFirstChild().getNodeName());
      Element entity = (Element) document.getFirstChild().getFirstChild();
      assertEquals(ENTITY, entity.getNodeName());
      assertEquals(Foo.class.getName(), entity.getAttribute(TYPE));

      Element property = (Element) entity.getFirstChild();
      assertEquals(PROPERTY, property.getNodeName());
      assertEquals("manyToMany", property.getAttribute(NAME));
      assertEquals(TRUE, property.getAttribute(N_TO_MANY));
      assertEquals(2, property.getAttributes().getLength());

      property = XmlUtils.getNextSiblingElement(property);
      assertEquals(PROPERTY, property.getNodeName());
      assertEquals("manyToOne", property.getAttribute(NAME));
      assertEquals("#{forgeInspectorTest$BarBean.all}", property.getAttribute(FACES_LOOKUP));
      assertEquals("#{forgeInspectorTest$BarBean.converter}", property.getAttribute(FACES_CONVERTER_ID));
      assertEquals(3, property.getAttributes().getLength());

      property = XmlUtils.getNextSiblingElement(property);
      assertEquals(PROPERTY, property.getNodeName());
      assertEquals("oneToMany", property.getAttribute(NAME));
      assertEquals(TRUE, property.getAttribute(N_TO_MANY));
      assertEquals(2, property.getAttributes().getLength());

      // oneToOne should not appear

      property = XmlUtils.getNextSiblingElement(property);
      assertEquals(PROPERTY, property.getNodeName());
      assertEquals("oneToOneMappedBy", property.getAttribute(NAME));
      assertEquals(TRUE, property.getAttribute(HIDDEN));
      assertEquals(2, property.getAttributes().getLength());

      assertEquals(4, entity.getChildNodes().getLength());
   }

   //
   // Inner class
   //

   static class Foo
   {
      @OneToOne(mappedBy = "foo")
      public Bar oneToOneMappedBy;

      @OneToOne
      public Bar oneToOne;

      @OneToMany
      public Set<Bar> oneToMany;

      @ManyToOne
      public Bar manyToOne;

      @ManyToMany
      public Bar manyToMany;
   }

   static class Bar
   {
      public String name;
   }
}
