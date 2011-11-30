package org.jboss.forge.scaffold.faces.metawidget.widgetbuilder;

import static org.metawidget.inspector.InspectionResultConstants.*;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.metawidget.inspector.annotation.UiComesAfter;
import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.faces.component.html.StaticHtmlMetawidget;
import org.metawidget.util.CollectionUtils;

public class ForgeWidgetBuilderTest
         extends TestCase
{
   //
   // Public methods
   //

   public void testCollection()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      ForgeWidgetBuilder widgetBuilder = new ForgeWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, List.class.getName());
      attributes.put(PARAMETERIZED_TYPE, Bar.class.getName());
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      // (this looks a little weird because 'Bar' is an inner class)

      String result = "<h:panelGroup><h:dataTable id=\"fooBar\" value=\"#{foo.bar}\" var=\"_item\">";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Name\"/></f:facet>";
      result += "<h:link outcome=\"/scaffold/forgeWidgetBuilderTest$Bar/view\"><f:param name=\"id\" value=\"#{_item.id}\"/><h:outputText value=\"#{_item.name}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Description\"/></f:facet>";
      result += "<h:link outcome=\"/scaffold/forgeWidgetBuilderTest$Bar/view\"><f:param name=\"id\" value=\"#{_item.id}\"/><h:outputText value=\"#{_item.description}\"/></h:link>";
      result += "</h:column>";
      result += "</h:dataTable>";
      result += "<h:selectOneMenu converter=\"#{forgeWidgetBuilderTest$BarBean.converter}\" value=\"#{requestScope['fooBarAdd']}\"><f:selectItem/><f:selectItems value=\"#{forgeWidgetBuilderTest$BarBean.all}\"/></h:selectOneMenu>";
      result += "<ui:param name=\"_entity\" value=\"#{foo}\"/>";
      result += "<h:commandLink action=\"#{_entity.addForgeWidgetBuilderTest$Bar(requestScope['fooBarAdd'])}\" value=\"Add\"/>";
      result += "</h:panelGroup>";

      assertEquals(result, widget.toString());
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
}
