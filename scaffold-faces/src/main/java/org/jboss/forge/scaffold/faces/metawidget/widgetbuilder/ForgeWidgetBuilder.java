/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

import static org.metawidget.inspector.InspectionResultConstants.*;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.*;

import java.util.List;
import java.util.Map;

import org.jboss.solder.core.Veto;
import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.StaticXmlMetawidget;
import org.metawidget.statically.StaticXmlWidget;
import org.metawidget.statically.faces.StaticFacesUtils;
import org.metawidget.statically.faces.component.StaticUIMetawidget;
import org.metawidget.statically.faces.component.html.layout.HtmlPanelGroup;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlColumn;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlCommandLink;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlDataTable;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlOutcomeTargetLink;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlOutputText;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlSelectOneMenu;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlWidgetBuilder;
import org.metawidget.statically.faces.component.html.widgetbuilder.Param;
import org.metawidget.statically.faces.component.widgetprocessor.ReadableIdProcessor;
import org.metawidget.statically.faces.component.widgetprocessor.StandardBindingProcessor;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.WidgetBuilderUtils;
import org.metawidget.util.simple.StringUtils;

/**
 * This class is marked <tt>&#64;Veto</tt> so that CDI doesn't get confused by Metawidget's jsr14 compilation and throw
 * a <tt>MalformedParameterizedTypeException</tt>.
 *
 * @author Richard Kennard
 */

@Veto
public class ForgeWidgetBuilder
         extends HtmlWidgetBuilder
{
   //
   // Public methods
   //

   @Override
   public StaticXmlWidget buildWidget(String elementName, Map<String, String> attributes, StaticXmlMetawidget metawidget)
   {
      // Render read-only <tt>FACES_LOOKUP</tt> as a link.

      String facesLookup = attributes.get(FACES_LOOKUP);

      if (facesLookup != null && WidgetBuilderUtils.isReadOnly(attributes))
      {
         if (StaticFacesUtils.isExpression(facesLookup))
         {
            String controllerName = ClassUtils.getSimpleName(WidgetBuilderUtils.getActualClassOrType(attributes));
            controllerName = StringUtils.decapitalize(controllerName);

            HtmlOutcomeTargetLink link = new HtmlOutcomeTargetLink();
            link.putAttribute("outcome", "/scaffold/" + controllerName + "/view");

            HtmlOutputText outputText = new HtmlOutputText();
            new StandardBindingProcessor().processWidget(outputText, elementName, attributes,
                     (StaticUIMetawidget) metawidget);

            Param param = new Param();
            param.putAttribute("name", "id");
            param.putAttribute("value",
                     StaticFacesUtils.wrapExpression(StaticFacesUtils.unwrapExpression(outputText.getValue()) + ".id"));
            link.getChildren().add(param);

            link.getChildren().add(outputText);

            return link;
         }
      }

      // Render collection tables with links

      String type = WidgetBuilderUtils.getActualClassOrType(attributes);

      if (type != null)
      {
         Class<?> clazz = ClassUtils.niceForName(type);

         if (clazz != null)
         {
            if (List.class.isAssignableFrom(clazz) /* || DataModel.class.isAssignableFrom( clazz ) */|| clazz.isArray())
            {
               return createDataTableComponent(attributes, metawidget);
            }
         }
      }

      // Delegate to next WidgetBuilder in the chain

      return null;
   }

   //
   // Protected methods
   //

   /**
    * Overridden to add row creation/deletion.
    */

   @Override
   protected StaticXmlWidget createDataTableComponent(Map<String, String> attributes, StaticXmlMetawidget metawidget)
   {
      // Create the normal table...

      StaticXmlWidget dataTable = super.createDataTableComponent(attributes, metawidget);

      String componentType = WidgetBuilderUtils.getComponentType(attributes);

      if (componentType == null)
      {
         return dataTable;
      }

      // ...then add row creation

      HtmlPanelGroup panelGroup = new HtmlPanelGroup();
      panelGroup.getChildren().add(dataTable);

      // Select menu at bottom

      HtmlSelectOneMenu select = new HtmlSelectOneMenu();
      new StandardBindingProcessor().processWidget(dataTable, PROPERTY, attributes, (StaticUIMetawidget) metawidget);
      new ReadableIdProcessor().processWidget(dataTable, PROPERTY, attributes, metawidget);
      String requestScopedValue = "requestScope['" + dataTable.getAttribute("id") + "Add']";
      select.setValue(StaticFacesUtils.wrapExpression(requestScopedValue));
      String simpleComponentType = ClassUtils.getSimpleName(componentType);
      String controllerName = StringUtils.decapitalize(simpleComponentType);
      select.setConverter(StaticFacesUtils.wrapExpression(controllerName + "Bean.converter"));
      Map<String, String> emptyAttributes = CollectionUtils.newHashMap();
      addSelectItems(select, StaticFacesUtils.wrapExpression(controllerName + "Bean.all"), emptyAttributes);
      panelGroup.getChildren().add(select);

      // Add link

      HtmlCommandLink addLink = new HtmlCommandLink();
      addLink.setValue("Add");
      String addExpression = StaticFacesUtils.unwrapExpression(((StaticUIMetawidget) metawidget).getValue());
      addExpression += ".add" + simpleComponentType + "(" + requestScopedValue + ")";
      addLink.putAttribute("action", StaticFacesUtils.wrapExpression(addExpression));
      panelGroup.getChildren().add(addLink);

      return panelGroup;
   }

   /**
    * Overridden to wrap column text with an <tt>h:link</tt>, in those cases we can determine the dataType.
    */

   @Override
   protected void addColumnComponent(HtmlDataTable dataTable, Map<String, String> tableAttributes, String elementName, Map<String, String> columnAttributes,
            StaticXmlMetawidget metawidget)
   {
      super.addColumnComponent(dataTable, tableAttributes, elementName, columnAttributes, metawidget);

      String componentType = WidgetBuilderUtils.getComponentType(tableAttributes);

      if (componentType != null)
      {
         String controllerName = StringUtils.decapitalize(ClassUtils.getSimpleName(componentType));

         HtmlOutcomeTargetLink link = new HtmlOutcomeTargetLink();
         link.putAttribute("outcome", "/scaffold/" + controllerName + "/view");

         Param param = new Param();
         param.putAttribute("name", "id");
         param.putAttribute("value", StaticFacesUtils.wrapExpression(dataTable.getAttribute("var") + ".id"));
         link.getChildren().add(param);

         List<StaticWidget> columns = dataTable.getChildren();
         HtmlColumn column = (HtmlColumn) columns.get(columns.size() - 1);
         link.getChildren().add(column.getChildren().remove(1));
         column.getChildren().add(link);
      }
   }
}
