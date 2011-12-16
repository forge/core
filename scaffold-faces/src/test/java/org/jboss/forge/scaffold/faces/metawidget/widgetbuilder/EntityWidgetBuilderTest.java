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
package org.jboss.forge.scaffold.faces.metawidget.widgetbuilder;

import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.*;
import static org.metawidget.inspector.InspectionResultConstants.*;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import junit.framework.TestCase;

import org.metawidget.inspector.annotation.UiComesAfter;
import org.metawidget.inspector.composite.CompositeInspector;
import org.metawidget.inspector.composite.CompositeInspectorConfig;
import org.metawidget.inspector.iface.Inspector;
import org.metawidget.inspector.impl.BaseObjectInspector;
import org.metawidget.inspector.impl.propertystyle.Property;
import org.metawidget.inspector.propertytype.PropertyTypeInspector;
import org.metawidget.statically.StaticMetawidget;
import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.StaticXmlStub;
import org.metawidget.statically.StaticXmlWidget;
import org.metawidget.statically.faces.component.html.StaticHtmlMetawidget;
import org.metawidget.util.CollectionUtils;

public class EntityWidgetBuilderTest
         extends TestCase
{
   //
   // Public methods
   //

   public void testManyToOne()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, Bar.class.getName());
      attributes.put(READ_ONLY, TRUE);
      attributes.put(FACES_LOOKUP, "#{barBean.all}");
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      String result = "<h:link outcome=\"/scaffold/entityWidgetBuilderTest$Bar/view\" value=\"#{foo.bar}\">";
      result += "<f:param name=\"id\" value=\"#{foo.bar.id}\"/>";
      result += "</h:link>";

      assertEquals(result, widget.toString());
   }

   public void testReadOnlyInverseOneToOne()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, Bar.class.getName());
      attributes.put(READ_ONLY, TRUE);
      attributes.put(INVERSE_RELATIONSHIP, TRUE);
      attributes.put(ONE_TO_ONE, TRUE);

      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      String result = "<h:link outcome=\"/scaffold/entityWidgetBuilderTest$Bar/view\" value=\"#{foo.bar}\">";
      result += "<f:param name=\"id\" value=\"#{foo.bar.id}\"/>";
      result += "</h:link>";

      assertEquals(result, widget.toString());

      // Should not show nested

      StaticHtmlMetawidget metawidget2 = new StaticHtmlMetawidget();
      Field parent = StaticMetawidget.class.getDeclaredField("mParent");
      parent.setAccessible(true);
      parent.set(metawidget2, metawidget);

      assertTrue(widgetBuilder.buildWidget(PROPERTY, attributes, metawidget2) instanceof StaticXmlStub);
   }

   public void testOptionalReadOnlyOneToOne()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      metawidget.setPath(FooOneToOne.class.getName());
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, Bar.class.getName());
      attributes.put(READ_ONLY, TRUE);
      attributes.put(ONE_TO_ONE, TRUE);

      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      String result = "<h:panelGrid columns=\"3\" rendered=\"#{!empty foo.bar}\">";
      result += "<h:outputLabel for=\"fooBarName\" value=\"Name:\"/>";
      result += "<h:outputText id=\"fooBarName\" value=\"#{foo.bar.name}\"/>";
      result += "<h:outputText/>";
      result += "<h:outputLabel for=\"fooBarDescription\" value=\"Description:\"/>";
      result += "<h:outputText id=\"fooBarDescription\" value=\"#{foo.bar.description}\"/>";
      result += "<h:outputText/>";
      result += "</h:panelGrid>";

      assertEquals(result, widget.toString());
   }

   public void testOneToOne()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      metawidget.setPath(FooOneToOne.class.getName());
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, Bar.class.getName());
      attributes.put(ONE_TO_ONE, TRUE);
      attributes.put(REQUIRED, TRUE);

      assertEquals(null, widgetBuilder.buildWidget(PROPERTY, attributes, metawidget));
   }

   public void testOptionalOneToOne()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      metawidget.setPath(FooOneToOne.class.getName());
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, Bar.class.getName());
      attributes.put(ONE_TO_ONE, TRUE);

      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      String result = "<h:panelGroup>";
      result += "<h:commandLink action=\"#{foo.newBar}\" rendered=\"#{empty foo.bar}\" value=\"Create New Bar\"/>";
      result += "<h:panelGrid columns=\"3\" rendered=\"#{!empty foo.bar}\">";
      result += "<h:outputLabel for=\"fooBarName\" value=\"Name:\"/>";
      result += "<h:panelGroup>";
      result += "<h:inputText id=\"fooBarName\" value=\"#{foo.bar.name}\"/>";
      result += "<h:message for=\"fooBarName\"/>";
      result += "</h:panelGroup>";
      result += "<h:outputText/>";
      result += "<h:outputLabel for=\"fooBarDescription\" value=\"Description:\"/>";
      result += "<h:panelGroup>";
      result += "<h:inputText id=\"fooBarDescription\" value=\"#{foo.bar.description}\"/>";
      result += "<h:message for=\"fooBarDescription\"/>";
      result += "</h:panelGroup>";
      result += "<h:outputText/>";
      result += "</h:panelGrid>";
      result += "</h:panelGroup>";

      assertEquals(result, widget.toString());
   }

   public void testTopLevelList()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bars");
      attributes.put(TYPE, List.class.getName());
      attributes.put(PARAMETERIZED_TYPE, Bar.class.getName());
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      // (this looks a little weird because 'Bar' is an inner class)

      String result = "<h:dataTable id=\"fooBars\" styleClass=\"data-table\" value=\"#{foo.bars}\" var=\"_item\">";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Name\"/></f:facet>";
      result += "<h:link outcome=\"/scaffold/entityWidgetBuilderTest$Bar/view\" value=\"#{_item.name}\"><f:param name=\"id\" value=\"#{_item.id}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Description\"/></f:facet>";
      result += "<h:link outcome=\"/scaffold/entityWidgetBuilderTest$Bar/view\" value=\"#{_item.description}\"><f:param name=\"id\" value=\"#{_item.id}\"/></h:link>";
      result += "</h:column>";
      result += "</h:dataTable>";

      assertEquals(result, widget.toString());
   }

   public void testEmbeddedSet()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bars");
      attributes.put(TYPE, Set.class.getName());
      attributes.put(PARAMETERIZED_TYPE, Bar.class.getName());
      attributes.put(N_TO_MANY, TRUE);
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      // (this looks a little weird because 'Bar' is an inner class)

      String result = "<h:panelGroup>";
      result += "<ui:param name=\"_collection\" value=\"#{foo.bars}\"/>";
      result += "<h:dataTable columnClasses=\",,remove-column\" id=\"fooBars\" styleClass=\"data-table\" value=\"#{forgeview:asList(_collection)}\" var=\"_item\">";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Name\"/></f:facet>";
      result += "<h:link outcome=\"/scaffold/entityWidgetBuilderTest$Bar/view\" value=\"#{_item.name}\"><f:param name=\"id\" value=\"#{_item.id}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Description\"/></f:facet>";
      result += "<h:link outcome=\"/scaffold/entityWidgetBuilderTest$Bar/view\" value=\"#{_item.description}\"><f:param name=\"id\" value=\"#{_item.id}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column><h:commandLink action=\"#{_collection.remove(_item)}\" styleClass=\"button\" value=\"Remove\"/></h:column>";
      result += "</h:dataTable>";
      result += "<h:panelGroup styleClass=\"buttons\">";
      result += "<h:selectOneMenu converter=\"#{entityWidgetBuilderTest$BarBean.converter}\" id=\"fooBarsAdd\" value=\"#{requestScope['fooBarsAdd']}\"><f:selectItem/><f:selectItems value=\"#{entityWidgetBuilderTest$BarBean.all}\"/></h:selectOneMenu>";
      result += "<h:commandLink action=\"#{_collection.add(requestScope['fooBarsAdd'])}\" onclick=\"if (document.getElementById(document.forms[0].id+':fooBarsAdd').selectedIndex &lt; 1) { alert('Must select a Entity Widget Builder Test$Bar'); return false; }\" value=\"Add\"/>";
      result += "</h:panelGroup>";
      result += "</h:panelGroup>";

      assertEquals(result, widget.toString());
      assertEquals(((StaticXmlWidget) widget.getChildren().get(1)).getAdditionalNamespaceURIs().get("forgeview"),
               "http://jboss.org/forge/view");
   }

   public void testSuppressOneToMany()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      Inspector testInspector = new BaseObjectInspector()
      {
         @Override
         protected Map<String, String> inspectProperty(Property property)
         {
            Map<String, String> attributes = CollectionUtils.newHashMap();

            // OneToMany

            if (property.isAnnotationPresent(OneToMany.class))
            {
               attributes.put(N_TO_MANY, TRUE);
            }

            return attributes;
         }
      };
      Inspector inspector = new CompositeInspector(new CompositeInspectorConfig()
               .setInspectors(
                        new PropertyTypeInspector(),
                        testInspector));

      metawidget.setInspector(inspector);
      metawidget.setValue("#{foo}");
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bars");
      attributes.put(TYPE, Set.class.getName());
      attributes.put(PARAMETERIZED_TYPE, FooOneToMany.class.getName());
      attributes.put(N_TO_MANY, TRUE);
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      // (this looks a little weird because 'FooOneToMany' is an inner class)

      String result = "<h:panelGroup>";
      result += "<ui:param name=\"_collection\" value=\"#{foo.bars}\"/>";
      result += "<h:dataTable columnClasses=\",,remove-column\" id=\"fooBars\" styleClass=\"data-table\" value=\"#{forgeview:asList(_collection)}\" var=\"_item\">";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Field 1\"/></f:facet>";
      result += "<h:link outcome=\"/scaffold/entityWidgetBuilderTest$FooOneToMany/view\" value=\"#{_item.field1}\"><f:param name=\"id\" value=\"#{_item.id}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Field 3\"/></f:facet>";
      result += "<h:link outcome=\"/scaffold/entityWidgetBuilderTest$FooOneToMany/view\" value=\"#{_item.field3}\"><f:param name=\"id\" value=\"#{_item.id}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column><h:commandLink action=\"#{_collection.remove(_item)}\" styleClass=\"button\" value=\"Remove\"/></h:column>";
      result += "</h:dataTable>";
      result += "<h:panelGroup styleClass=\"buttons\">";
      result += "<h:selectOneMenu converter=\"#{entityWidgetBuilderTest$FooOneToManyBean.converter}\" id=\"fooBarsAdd\" value=\"#{requestScope['fooBarsAdd']}\"><f:selectItem/><f:selectItems value=\"#{entityWidgetBuilderTest$FooOneToManyBean.all}\"/></h:selectOneMenu>";
      result += "<h:commandLink action=\"#{_collection.add(requestScope['fooBarsAdd'])}\" onclick=\"if (document.getElementById(document.forms[0].id+':fooBarsAdd').selectedIndex &lt; 1) { alert('Must select a Entity Widget Builder Test$Foo One To Many'); return false; }\" value=\"Add\"/>";
      result += "</h:panelGroup>";
      result += "</h:panelGroup>";

      assertEquals(result, widget.toString());
      assertEquals(((StaticXmlWidget) widget.getChildren().get(1)).getAdditionalNamespaceURIs().get("forgeview"),
               "http://jboss.org/forge/view");
   }

   //
   // Inner class
   //

   static class Bar
   {
      public String name;

      @UiComesAfter("name")
      public String description;
   }

   static class FooOneToMany
   {
      public String field1;

      @OneToMany
      public Set<String> field2;

      public String field3;
   }

   static class FooOneToOne
   {
      @OneToOne
      public Bar bar;
   }
}
