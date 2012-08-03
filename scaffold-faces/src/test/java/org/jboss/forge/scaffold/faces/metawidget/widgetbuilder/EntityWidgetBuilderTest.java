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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.OneToOne;

import junit.framework.TestCase;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.shell.env.ConfigurationAdapter;
import org.metawidget.inspector.annotation.MetawidgetAnnotationInspector;
import org.metawidget.inspector.annotation.UiComesAfter;
import org.metawidget.inspector.annotation.UiRequired;
import org.metawidget.inspector.composite.CompositeInspector;
import org.metawidget.inspector.composite.CompositeInspectorConfig;
import org.metawidget.inspector.iface.Inspector;
import org.metawidget.inspector.impl.BaseObjectInspector;
import org.metawidget.inspector.impl.BaseObjectInspectorConfig;
import org.metawidget.inspector.impl.propertystyle.Property;
import org.metawidget.inspector.impl.propertystyle.statically.StaticPropertyStyle;
import org.metawidget.inspector.propertytype.PropertyTypeInspector;
import org.metawidget.statically.BaseStaticWidget;
import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.StaticXmlStub;
import org.metawidget.statically.StaticXmlWidget;
import org.metawidget.statically.faces.component.html.StaticHtmlMetawidget;
import org.metawidget.statically.faces.component.widgetprocessor.RequiredAttributeProcessor;
import org.metawidget.util.CollectionUtils;

public class EntityWidgetBuilderTest
         extends TestCase
{
   //
   // Public methods
   //

   EntityWidgetBuilderConfig config = new EntityWidgetBuilderConfig().setConfig(new MockForgeConfiguration());

   public void testManyToOne()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder(this.config);
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, Bar.class.getName());
      attributes.put(READ_ONLY, TRUE);
      attributes.put(FACES_LOOKUP, "#{barBean.all}");
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      String result = "<h:link outcome=\"/entityWidgetBuilderTest$Bar/view\" value=\"#{foo.bar}\">";
      result += "<f:param name=\"id\" value=\"#{foo.bar.id}\"/>";
      result += "</h:link>";

      assertEquals(result, widget.toString());
   }

   public void testReadOnlyInverseOneToOne()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      metawidget.setPath(FooOneToOne.class.getName());
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder(new EntityWidgetBuilderConfig());
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, Bar.class.getName());
      attributes.put(READ_ONLY, TRUE);
      attributes.put(INVERSE_RELATIONSHIP, TRUE);
      attributes.put(ONE_TO_ONE, TRUE);

      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      // Same as non-inverse, for now

      String result = "<h:panelGrid columns=\"3\" rendered=\"#{!empty foo.bar}\">";
      result += "<h:outputLabel for=\"fooBarName\" value=\"Name:\"/>";
      result += "<h:outputText id=\"fooBarName\" value=\"#{foo.bar.name}\"/>";
      result += "<h:outputText/><h:outputLabel for=\"fooBarDescription\" value=\"Description:\"/>";
      result += "<h:outputText id=\"fooBarDescription\" value=\"#{foo.bar.description}\"/>";
      result += "<h:outputText/>";
      result += "</h:panelGrid>";

      assertEquals(result, widget.toString());

      // Should not show nested

      StaticHtmlMetawidget metawidget2 = new StaticHtmlMetawidget();
      Method parent = BaseStaticWidget.class.getDeclaredMethod("setParent", StaticWidget.class);
      parent.setAccessible(true);
      parent.invoke(metawidget2, metawidget);

      assertTrue(widgetBuilder.buildWidget(PROPERTY, attributes, metawidget2) instanceof StaticXmlStub);
   }

   public void testOptionalReadOnlyOneToOne()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      metawidget.setPath(FooOneToOne.class.getName());
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder(new EntityWidgetBuilderConfig());
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
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder(new EntityWidgetBuilderConfig());
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
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder(new EntityWidgetBuilderConfig());
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
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder(this.config);
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bars");
      attributes.put(TYPE, List.class.getName());
      attributes.put(PARAMETERIZED_TYPE, Bar.class.getName());
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      // (this looks a little weird because 'Bar' is an inner class)

      String result = "<h:dataTable id=\"fooBars\" styleClass=\"data-table\" value=\"#{foo.bars}\" var=\"_item\">";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Name\"/></f:facet>";
      result += "<h:link outcome=\"/entityWidgetBuilderTest$Bar/view\"><f:param name=\"id\" value=\"#{_item.id}\"/>";
      result += "<h:outputText id=\"itemName\" value=\"#{_item.name}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Description\"/></f:facet>";
      result += "<h:link outcome=\"/entityWidgetBuilderTest$Bar/view\"><f:param name=\"id\" value=\"#{_item.id}\"/>";
      result += "<h:outputText id=\"itemDescription\" value=\"#{_item.description}\"/></h:link>";
      result += "</h:column>";
      result += "</h:dataTable>";

      assertEquals(result, widget.toString());
   }

   public void testEmbeddedSet()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder(this.config);
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bars");
      attributes.put(TYPE, Set.class.getName());
      attributes.put(PARAMETERIZED_TYPE, Bar.class.getName());
      attributes.put(N_TO_MANY, TRUE);
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      // (this looks a little weird because 'Bar' is an inner class)

      String result = "<h:panelGroup>";
      result += "<ui:param name=\"_collection\" value=\"#{foo.bars}\"/>";
      result += "<h:dataTable id=\"fooBars\" styleClass=\"data-table\" value=\"#{forgeview:asList(_collection)}\" var=\"_item\">";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Name\"/></f:facet>";
      result += "<h:link outcome=\"/entityWidgetBuilderTest$Bar/view\"><f:param name=\"id\" value=\"#{_item.id}\"/>";
      result += "<h:outputText id=\"itemName\" value=\"#{_item.name}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Description\"/></f:facet>";
      result += "<h:link outcome=\"/entityWidgetBuilderTest$Bar/view\"><f:param name=\"id\" value=\"#{_item.id}\"/>";
      result += "<h:outputText id=\"itemDescription\" value=\"#{_item.description}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column footerClass=\"remove-column\" headerClass=\"remove-column\"><h:commandLink action=\"#{_collection.remove(_item)}\" styleClass=\"remove-button\"/></h:column>";
      result += "</h:dataTable>";
      result += "<h:panelGrid columnClasses=\",remove-column\" columns=\"2\" styleClass=\"data-table-footer\">";
      result += "<h:selectOneMenu converter=\"#{entityWidgetBuilderTest$BarBean.converter}\" id=\"fooBarsSelect\" value=\"#{requestScope['fooBarsSelect']}\"><f:selectItem/><f:selectItems value=\"#{entityWidgetBuilderTest$BarBean.all}\"/></h:selectOneMenu>";
      result += "<h:commandLink action=\"#{_collection.add(requestScope['fooBarsSelect'])}\" id=\"fooBarsAdd\" onclick=\"if (document.getElementById(document.forms[0].id+':fooBarsSelect').selectedIndex &lt; 1) { alert('Must select a Entity Widget Builder Test$Bar'); return false; }\" styleClass=\"add-button\"/>";
      result += "</h:panelGrid>";
      result += "</h:panelGroup>";

      assertEquals(result, widget.toString());
      assertEquals(((StaticXmlWidget) widget.getChildren().get(1)).getAdditionalNamespaceURIs().get("forgeview"),
               "http://jboss.org/forge/view");

      // With suppressed column

      attributes.put(INVERSE_RELATIONSHIP, "name");
      widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      result = "<h:panelGroup>";
      result += "<ui:param name=\"_collection\" value=\"#{foo.bars}\"/>";
      result += "<h:dataTable id=\"fooBars\" styleClass=\"data-table\" value=\"#{forgeview:asList(_collection)}\" var=\"_item\">";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Description\"/></f:facet>";
      result += "<h:link outcome=\"/entityWidgetBuilderTest$Bar/view\"><f:param name=\"id\" value=\"#{_item.id}\"/>";
      result += "<h:outputText id=\"itemDescription\" value=\"#{_item.description}\"/></h:link>";
      result += "<f:facet name=\"footer\">";
      result += "<h:inputText id=\"entityWidgetBuilderTestBarBeanAddDescription\" value=\"#{entityWidgetBuilderTest$BarBean.add.description}\"/>";
      result += "<h:message for=\"entityWidgetBuilderTestBarBeanAddDescription\" styleClass=\"error\"/>";
      result += "</f:facet>";
      result += "</h:column>";
      result += "<h:column footerClass=\"remove-column\" headerClass=\"remove-column\"><h:commandLink action=\"#{_collection.remove(_item)}\" styleClass=\"remove-button\"/>";
      result += "<f:facet name=\"footer\">";
      result += "<h:commandLink action=\"#{_collection.add(entityWidgetBuilderTest$BarBean.added)}\" id=\"fooBarsAdd\" styleClass=\"add-button\">";
      result += "<f:setPropertyActionListener target=\"#{entityWidgetBuilderTest$BarBean.add.name}\" value=\"#{foo}\"/>";
      result += "</h:commandLink>";
      result += "</f:facet>";
      result += "</h:column>";
      result += "</h:dataTable>";
      result += "</h:panelGroup>";

      assertEquals(result, widget.toString());

      // Read-only

      metawidget.setReadOnly(true);
      widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      result = "<h:dataTable id=\"fooBars\" styleClass=\"data-table\" value=\"#{forgeview:asList(foo.bars)}\" var=\"_item\">";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Description\"/></f:facet>";
      result += "<h:link outcome=\"/entityWidgetBuilderTest$Bar/view\"><f:param name=\"id\" value=\"#{_item.id}\"/>";
      result += "<h:outputText id=\"itemDescription\" value=\"#{_item.description}\"/></h:link>";
      result += "</h:column>";
      result += "</h:dataTable>";

      assertEquals(result, widget.toString());
      metawidget.setReadOnly(false);

      // With 'required' column (should suppress required=\"true\")

      attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bars");
      attributes.put(TYPE, Set.class.getName());
      attributes.put(PARAMETERIZED_TYPE, RequiredBar.class.getName());
      attributes.put(N_TO_MANY, TRUE);
      attributes.put(INVERSE_RELATIONSHIP, "foo");
      widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      result = "<h:panelGroup>";
      result += "<ui:param name=\"_collection\" value=\"#{foo.bars}\"/>";
      result += "<h:dataTable id=\"fooBars\" styleClass=\"data-table\" value=\"#{forgeview:asList(_collection)}\" var=\"_item\">";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Name\"/></f:facet>";
      result += "<h:link outcome=\"/entityWidgetBuilderTest$RequiredBar/view\"><f:param name=\"id\" value=\"#{_item.id}\"/>";
      result += "<h:outputText id=\"itemName\" value=\"#{_item.name}\"/></h:link>";
      result += "<f:facet name=\"footer\">";
      result += "<h:inputText id=\"entityWidgetBuilderTestRequiredBarBeanAddName\" value=\"#{entityWidgetBuilderTest$RequiredBarBean.add.name}\"/>";
      result += "<h:message for=\"entityWidgetBuilderTestRequiredBarBeanAddName\" styleClass=\"error\"/>";
      result += "</f:facet>";
      result += "</h:column>";
      result += "<h:column footerClass=\"remove-column\" headerClass=\"remove-column\"><h:commandLink action=\"#{_collection.remove(_item)}\" styleClass=\"remove-button\"/><f:facet name=\"footer\"><h:commandLink action=\"#{_collection.add(entityWidgetBuilderTest$RequiredBarBean.added)}\" id=\"fooBarsAdd\" styleClass=\"add-button\"><f:setPropertyActionListener target=\"#{entityWidgetBuilderTest$RequiredBarBean.add.foo}\" value=\"#{foo}\"/></h:commandLink></f:facet></h:column>";
      result += "</h:dataTable>";
      result += "</h:panelGroup>";

      assertEquals(result, widget.toString());
      assertEquals(((StaticXmlWidget) widget.getChildren().get(1)).getAdditionalNamespaceURIs().get("forgeview"),
               "http://jboss.org/forge/view");
      assertTrue(metawidget.getWidgetProcessor(RequiredAttributeProcessor.class) != null);
   }

   public void testSuppressOneToMany()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder(this.config);
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bars");
      attributes.put(TYPE, Set.class.getName());
      attributes.put(PARAMETERIZED_TYPE, FooOneToMany.class.getName());
      attributes.put(N_TO_MANY, TRUE);
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      // (this looks a little weird because 'FooOneToMany' is an inner class)

      String result = "<h:panelGroup>";
      result += "<ui:param name=\"_collection\" value=\"#{foo.bars}\"/>";
      result += "<h:dataTable id=\"fooBars\" styleClass=\"data-table\" value=\"#{forgeview:asList(_collection)}\" var=\"_item\">";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Field 1\"/></f:facet>";
      result += "<h:link outcome=\"/entityWidgetBuilderTest$FooOneToMany/view\"><f:param name=\"id\" value=\"#{_item.id}\"/><h:outputText id=\"itemField1\" value=\"#{_item.field1}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Field 3\"/></f:facet>";
      result += "<h:link outcome=\"/entityWidgetBuilderTest$FooOneToMany/view\"><f:param name=\"id\" value=\"#{_item.id}\"/><h:outputText id=\"itemField3\" value=\"#{_item.field3}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column footerClass=\"remove-column\" headerClass=\"remove-column\"><h:commandLink action=\"#{_collection.remove(_item)}\" styleClass=\"remove-button\"/></h:column>";
      result += "</h:dataTable>";
      result += "<h:panelGrid columnClasses=\",remove-column\" columns=\"2\" styleClass=\"data-table-footer\">";
      result += "<h:selectOneMenu converter=\"#{entityWidgetBuilderTest$FooOneToManyBean.converter}\" id=\"fooBarsSelect\" value=\"#{requestScope['fooBarsSelect']}\"><f:selectItem/><f:selectItems value=\"#{entityWidgetBuilderTest$FooOneToManyBean.all}\"/></h:selectOneMenu>";
      result += "<h:commandLink action=\"#{_collection.add(requestScope['fooBarsSelect'])}\" id=\"fooBarsAdd\" onclick=\"if (document.getElementById(document.forms[0].id+':fooBarsSelect').selectedIndex &lt; 1) { alert('Must select a Entity Widget Builder Test$Foo One To Many'); return false; }\" styleClass=\"add-button\"/>";
      result += "</h:panelGrid>";
      result += "</h:panelGroup>";

      assertEquals(result, widget.toString());
      assertEquals(((StaticXmlWidget) widget.getChildren().get(1)).getAdditionalNamespaceURIs().get("forgeview"),
               "http://jboss.org/forge/view");
   }

   public void testExpandOneToOne()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      Inspector testInspector = new BaseObjectInspector()
      {
         @Override
         protected Map<String, String> inspectProperty(Property property)
         {
            Map<String, String> attributes = CollectionUtils.newHashMap();

            // OneToOne

            if (property.isAnnotationPresent(OneToOne.class))
            {
               attributes.put(ONE_TO_ONE, TRUE);
            }

            return attributes;
         }
      };
      Inspector inspector = new CompositeInspector(new CompositeInspectorConfig()
               .setInspectors(
                        new PropertyTypeInspector(new BaseObjectInspectorConfig()
                                 .setPropertyStyle(new StaticPropertyStyle())),
                        new MetawidgetAnnotationInspector(new BaseObjectInspectorConfig()
                                 .setPropertyStyle(new StaticPropertyStyle())),
                        testInspector));

      metawidget.setInspector(inspector);
      metawidget.setValue("#{foo}");
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder(this.config);
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bars");
      attributes.put(TYPE, Set.class.getName());
      attributes.put(PARAMETERIZED_TYPE, FooOneToOne.class.getName());
      attributes.put(N_TO_MANY, TRUE);
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      // (this looks a little weird because 'FooOneToOne' is an inner class)

      String result = "<h:panelGroup>";
      result += "<ui:param name=\"_collection\" value=\"#{foo.bars}\"/>";
      result += "<h:dataTable id=\"fooBars\" styleClass=\"data-table\" value=\"#{forgeview:asList(_collection)}\" var=\"_item\">";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Name\"/></f:facet>";
      result += "<h:link outcome=\"/entityWidgetBuilderTest$FooOneToOne/view\"><f:param name=\"id\" value=\"#{_item.id}\"/><h:outputText id=\"itemName\" value=\"#{_item.name}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Description\"/></f:facet>";
      result += "<h:link outcome=\"/entityWidgetBuilderTest$FooOneToOne/view\"><f:param name=\"id\" value=\"#{_item.id}\"/><h:outputText id=\"itemDescription\" value=\"#{_item.description}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column footerClass=\"remove-column\" headerClass=\"remove-column\"><h:commandLink action=\"#{_collection.remove(_item)}\" styleClass=\"remove-button\"/></h:column>";
      result += "</h:dataTable>";
      result += "<h:panelGrid columnClasses=\",remove-column\" columns=\"2\" styleClass=\"data-table-footer\">";
      result += "<h:selectOneMenu converter=\"#{entityWidgetBuilderTest$FooOneToOneBean.converter}\" id=\"fooBarsSelect\" value=\"#{requestScope['fooBarsSelect']}\"><f:selectItem/><f:selectItems value=\"#{entityWidgetBuilderTest$FooOneToOneBean.all}\"/></h:selectOneMenu>";
      result += "<h:commandLink action=\"#{_collection.add(requestScope['fooBarsSelect'])}\" id=\"fooBarsAdd\" onclick=\"if (document.getElementById(document.forms[0].id+':fooBarsSelect').selectedIndex &lt; 1) { alert('Must select a Entity Widget Builder Test$Foo One To One'); return false; }\" styleClass=\"add-button\"/>";
      result += "</h:panelGrid>";
      result += "</h:panelGroup>";

      assertEquals(result, widget.toString());
   }

   public void testReadOnlyBoolean()
            throws Exception
   {
      // Normal boolean

      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      EntityWidgetBuilder widgetBuilder = new EntityWidgetBuilder(new EntityWidgetBuilderConfig());
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, boolean.class.getName());
      assertEquals(null, widgetBuilder.buildWidget(PROPERTY, attributes, metawidget));

      // Read-only boolean

      attributes.put(READ_ONLY, TRUE);
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      String result = "<h:outputText styleClass=\"#{foo.bar ? 'boolean-true' : 'boolean-false'}\" value=\"\"/>";
      assertEquals(result, widget.toString());
   }

   public void testConfig()
   {
      EntityWidgetBuilderConfig config1 = new EntityWidgetBuilderConfig();
      EntityWidgetBuilderConfig config2 = new EntityWidgetBuilderConfig();

      assertTrue(config1.equals(config2));
      assertEquals(config1.hashCode(), config2.hashCode());
      assertTrue(!config1.equals("Foo"));
      Configuration forgeConfig = new ConfigurationAdapter(null);
      config1.setConfig(forgeConfig);
      assertTrue(!config1.equals(config2));
      config2.setConfig(forgeConfig);
      assertTrue(config1.equals(config2));
      assertEquals(config1.hashCode(), config2.hashCode());
   }

   //
   // Inner class
   //

   static class Bar
   {
      public String getName()
      {
         return null;
      }

      public void setName(String name)
      {
         // Do nothing
      }

      @UiComesAfter("name")
      public String getDescription()
      {
         return null;
      }

      public void setDescription(String description)
      {
         // Do nothing
      }
   }

   static class FooOneToMany
   {
      public String getField1()
      {
         return null;
      }

      // Not @OneToMany: sometimes annotations are forgotten
      public Set<String> getField2()
      {
         return null;
      }

      public String getField3()
      {
         return null;
      }
   }

   static class FooOneToOne
   {
      @OneToOne
      public Bar getBar()
      {
         return null;
      }
   }

   static class RequiredBar
   {
      @UiRequired
      public String getName()
      {
         return null;
      }

      public void setName(String name)
      {
         // Do nothing
      }
   }
}
