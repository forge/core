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

import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.*;
import static org.metawidget.inspector.InspectionResultConstants.*;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.solder.core.Veto;
import org.metawidget.statically.BaseStaticXmlWidget;
import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.StaticXmlMetawidget;
import org.metawidget.statically.StaticXmlWidget;
import org.metawidget.statically.faces.StaticFacesUtils;
import org.metawidget.statically.faces.component.StaticUIMetawidget;
import org.metawidget.statically.faces.component.ValueHolder;
import org.metawidget.statically.faces.component.html.layout.HtmlPanelGroup;
import org.metawidget.statically.faces.component.html.widgetbuilder.FaceletsParam;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlColumn;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlCommandLink;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlDataTable;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlOutcomeTargetLink;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlSelectOneMenu;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlWidgetBuilder;
import org.metawidget.statically.faces.component.html.widgetbuilder.Param;
import org.metawidget.statically.faces.component.widgetprocessor.ReadableIdProcessor;
import org.metawidget.statically.faces.component.widgetprocessor.StandardBindingProcessor;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.WidgetBuilderUtils;
import org.metawidget.util.simple.StringUtils;
import org.w3c.dom.NodeList;

/**
 * Builds widgets with Forge-specific behaviours (such as links to other scaffolding pages).
 * <p>
 * This class is marked <tt>&#64;Veto</tt> so that CDI doesn't get confused by Metawidget's jsr14 compilation and throw
 * a <tt>MalformedParameterizedTypeException</tt>.
 *
 * @author Richard Kennard
 */

@Veto
public class EntityWidgetBuilder
         extends HtmlWidgetBuilder
{
   //
   // Private statics
   //

   private static final String COLLECTION_VAR = "_collection";

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
            new StandardBindingProcessor().processWidget(link, elementName, attributes,
                     (StaticUIMetawidget) metawidget);

            Param param = new Param();
            param.putAttribute("name", "id");
            param.putAttribute("value",
                     StaticFacesUtils.wrapExpression(StaticFacesUtils.unwrapExpression(link.getValue()) + ".id"));
            link.getChildren().add(param);

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
            if (Collection.class.isAssignableFrom(clazz))
            {
               return createDataTableComponent(elementName, attributes, metawidget);
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
   protected StaticXmlWidget createDataTableComponent(String elementName, Map<String, String> attributes,
            StaticXmlMetawidget metawidget)
   {
      // Create the normal table

      StaticXmlWidget dataTable = super.createDataTableComponent(elementName, attributes, metawidget);
      dataTable.putAttribute("styleClass", "data-table");

      // Process the binding and id early, so we can use them below

      new StandardBindingProcessor().processWidget(dataTable, elementName, attributes, (StaticUIMetawidget) metawidget);
      new ReadableIdProcessor().processWidget(dataTable, elementName, attributes, metawidget);

      ValueHolder valueHolderTable = (ValueHolder) dataTable;
      String tableValueExpression = valueHolderTable.getValue();

      // Special support for non-Lists

      Class<?> clazz = ClassUtils.niceForName(WidgetBuilderUtils.getActualClassOrType(attributes));

      if (!List.class.isAssignableFrom(clazz))
      {
         String asListValueExpression = "forgeview:asList(" + StaticFacesUtils.unwrapExpression(tableValueExpression)
                  + ")";
         valueHolderTable.setValue(StaticFacesUtils.wrapExpression(asListValueExpression));
         ((BaseStaticXmlWidget) dataTable).putAdditionalNamespaceURI("forgeview", "http://jboss.org/forge/view");
      }

      // Add row creation/deletion for OneToMany and ManyToMany

      if (!TRUE.equals(attributes.get(N_TO_MANY)) || metawidget.isReadOnly())
      {
         return dataTable;
      }

      String componentType = WidgetBuilderUtils.getComponentType(attributes);

      if (componentType == null)
      {
         return dataTable;
      }

      HtmlPanelGroup panelGroup = new HtmlPanelGroup();

      // Hack until https://issues.apache.org/jira/browse/MYFACES-3410 is resolved

      FaceletsParam param = new FaceletsParam();
      param.putAttribute("name", COLLECTION_VAR);
      param.putAttribute("value", tableValueExpression);
      panelGroup.getChildren().add(param);

      // Special support for non-Lists

      if (!List.class.isAssignableFrom(clazz))
      {
         valueHolderTable.setValue(StaticFacesUtils.wrapExpression("forgeview:asList(" + COLLECTION_VAR + ")"));
      }
      else
      {
         valueHolderTable.setValue(StaticFacesUtils.wrapExpression(COLLECTION_VAR));
      }

      panelGroup.getChildren().add(dataTable);

      // Select menu at bottom

      HtmlSelectOneMenu select = new HtmlSelectOneMenu();
      select.putAttribute("styleClass", "select-add");
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
      addLink.putAttribute("styleClass", "button");
      String addExpression = COLLECTION_VAR + ".add(" + requestScopedValue + ")";
      addLink.putAttribute("action", StaticFacesUtils.wrapExpression(addExpression));
      panelGroup.getChildren().add(addLink);

      return panelGroup;
   }

   /**
    * Overridden to add a 'delete' column.
    */

   @Override
   protected void addColumnComponents(HtmlDataTable dataTable, Map<String, String> attributes, NodeList elements,
            StaticXmlMetawidget metawidget)
   {
      super.addColumnComponents(dataTable, attributes, elements, metawidget);

      if (dataTable.getChildren().isEmpty())
      {
         return;
      }

      if (!attributes.containsKey(N_TO_MANY) || metawidget.isReadOnly())
      {
         return;
      }

      HtmlCommandLink removeLink = new HtmlCommandLink();
      removeLink.setValue("Remove");
      removeLink.putAttribute("styleClass", "button");
      String removeExpression = COLLECTION_VAR + ".remove(" + dataTable.getAttribute("var") + ")";
      removeLink.putAttribute("action", StaticFacesUtils.wrapExpression(removeExpression));

      HtmlColumn column = new HtmlColumn();
      column.getChildren().add(removeLink);

      // CSS

      char[] commas = new char[dataTable.getChildren().size()];
      Arrays.fill(commas, ',');
      dataTable.putAttribute("columnClasses", String.valueOf(commas) + "remove-column");
      dataTable.getChildren().add(column);
   }

   /**
    * Overridden to replace original column text with an <tt>h:link</tt>, in those cases we can determine the dataType.
    */

   @Override
   protected void addColumnComponent(HtmlDataTable dataTable, Map<String, String> tableAttributes, String elementName,
            Map<String, String> columnAttributes,
            StaticXmlMetawidget metawidget)
   {
      // Suppress columns that show Collection values (their toString is never very nice)

      if (TRUE.equals(columnAttributes.get(N_TO_MANY)))
      {
         return;
      }

      super.addColumnComponent(dataTable, tableAttributes, elementName, columnAttributes, metawidget);

      String componentType = WidgetBuilderUtils.getComponentType(tableAttributes);

      if (componentType != null)
      {
         String controllerName = StringUtils.decapitalize(ClassUtils.getSimpleName(componentType));

         // Get the original column text...

         List<StaticWidget> columns = dataTable.getChildren();
         HtmlColumn column = (HtmlColumn) columns.get(columns.size() - 1);
         ValueHolder originalComponent = (ValueHolder) column.getChildren().remove(1);

         // ...and create a link with the same value...

         HtmlOutcomeTargetLink link = new HtmlOutcomeTargetLink();
         link.putAttribute("outcome", "/scaffold/" + controllerName + "/view");
         link.setValue(originalComponent.getValue());

         // ...pointing to the id

         Param param = new Param();
         param.putAttribute("name", "id");
         param.putAttribute("value", StaticFacesUtils.wrapExpression(dataTable.getAttribute("var") + ".id"));
         link.getChildren().add(param);

         column.getChildren().add(link);
      }
   }
}
