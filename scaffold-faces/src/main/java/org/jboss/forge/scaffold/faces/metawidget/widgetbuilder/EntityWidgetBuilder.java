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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.scaffold.faces.FacesScaffold;
import org.metawidget.statically.BaseStaticXmlWidget;
import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.StaticXmlMetawidget;
import org.metawidget.statically.StaticXmlStub;
import org.metawidget.statically.StaticXmlWidget;
import org.metawidget.statically.faces.StaticFacesUtils;
import org.metawidget.statically.faces.component.StaticUIMetawidget;
import org.metawidget.statically.faces.component.ValueHolder;
import org.metawidget.statically.faces.component.html.StaticHtmlMetawidget;
import org.metawidget.statically.faces.component.html.layout.HtmlMessage;
import org.metawidget.statically.faces.component.html.layout.HtmlPanelGrid;
import org.metawidget.statically.faces.component.html.layout.HtmlPanelGroup;
import org.metawidget.statically.faces.component.html.widgetbuilder.FaceletsParam;
import org.metawidget.statically.faces.component.html.widgetbuilder.Facet;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlColumn;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlCommandLink;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlDataTable;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlOutcomeTargetLink;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlOutputText;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlSelectOneMenu;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlWidgetBuilder;
import org.metawidget.statically.faces.component.html.widgetbuilder.Param;
import org.metawidget.statically.faces.component.widgetprocessor.ReadableIdProcessor;
import org.metawidget.statically.faces.component.widgetprocessor.RequiredAttributeProcessor;
import org.metawidget.statically.faces.component.widgetprocessor.StandardBindingProcessor;
import org.metawidget.statically.layout.SimpleLayout;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.WidgetBuilderUtils;
import org.metawidget.util.XmlUtils;
import org.metawidget.util.simple.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Builds widgets with Forge-specific behaviours (such as links to other scaffolding pages).
 *
 * @author Richard Kennard
 */

public class EntityWidgetBuilder
         extends HtmlWidgetBuilder
{
   //
   // Private statics
   //

   private static final String COLLECTION_VAR = "_collection";

   /**
    * When expanding OneToOne or Embedded types in data table rows, we must point the row link to the original type, not
    * the type being expanded
    */

   private static final String TOP_LEVEL_PARAMETERIZED_TYPE = "top-level-parameterized-type";

   /**
    * Current Forge Configuration. Useful to retrieve <code>targetDir</code>.
    */

   private final Configuration config;

   //
   // Constructor
   //

   public EntityWidgetBuilder(EntityWidgetBuilderConfig config)
   {
      this.config = config.getConfig();
   }

   //
   // Public methods
   //

   @Override
   public StaticXmlWidget buildWidget(String elementName, Map<String, String> attributes, StaticXmlMetawidget metawidget)
   {
      // Suppress nested INVERSE ONE_TO_ONE, to avoid recursion

      if (TRUE.equals(attributes.get(ONE_TO_ONE)) && attributes.containsKey(INVERSE_RELATIONSHIP)
               && metawidget.getParent() != null)
      {
         return new StaticXmlStub();
      }

      String type = attributes.get(TYPE);

      if (WidgetBuilderUtils.isReadOnly(attributes))
      {
         // Render read-only FACES_LOOKUP as a link

         if (attributes.containsKey(FACES_LOOKUP))
         {
            // Cleaner to stop using a Converter for a read-only FACES_LOOKUP, than to make every Converter consider
            // whether it's really a UIInput (and should therefore use .toString instead of .getId())

            attributes.remove(FACES_CONVERTER_ID);

            // (unless parent is *already* a link, such as inside a table row)

            if (metawidget.getParent() instanceof HtmlOutcomeTargetLink)
            {
               return null;
            }

            String controllerName = ClassUtils.getSimpleName(attributes.get(TYPE));
            controllerName = StringUtils.decapitalize(controllerName);

            HtmlOutcomeTargetLink link = new HtmlOutcomeTargetLink();
            link.putAttribute("outcome", getTargetDir() + "/" + controllerName + "/view");

            StandardBindingProcessor bindingProcessor = metawidget.getWidgetProcessor(StandardBindingProcessor.class);

            if (bindingProcessor != null)
            {
               bindingProcessor.processWidget(link, elementName, attributes,
                        (StaticUIMetawidget) metawidget);
            }

            String reverseKey = "id";
            if (attributes.containsKey(REVERSE_PRIMARY_KEY))
            {
               reverseKey = attributes.get(REVERSE_PRIMARY_KEY);
            }

            Param param = new Param();
            param.putAttribute("name", "id");
            param.putAttribute(
                     "value",
                     StaticFacesUtils.wrapExpression(StaticFacesUtils.unwrapExpression(link.getValue()) + StringUtils.SEPARATOR_DOT_CHAR
                              + reverseKey));
            link.getChildren().add(param);

            return link;
         }

         Class<?> clazz = ClassUtils.niceForName(type);

         if (clazz != null)
         {
            // Render read-only booleans as graphics

            if (boolean.class.equals(clazz))
            {
               HtmlOutputText outputText = new HtmlOutputText();
               StandardBindingProcessor bindingProcessor = metawidget
                        .getWidgetProcessor(StandardBindingProcessor.class);

               if (bindingProcessor != null)
               {
                  bindingProcessor.processWidget(outputText, elementName, attributes, (StaticUIMetawidget) metawidget);
               }

               String styleClassEl = StaticFacesUtils.unwrapExpression(outputText.getValue())
                        + " ? 'boolean-true' : 'boolean-false'";
               outputText.putAttribute("styleClass", StaticFacesUtils.wrapExpression(styleClassEl));
               outputText.setValue("");

               return outputText;
            }
         }
      }

      // Render collection tables with links

      if (type != null)
      {
         // Render non-optional ONE_TO_ONE with a button

         if (TRUE.equals(attributes.get(ONE_TO_ONE)) && !TRUE.equals(attributes.get(REQUIRED)))
         {
            // (we are about to create a nestedMetawidget, so we must prevent recursion)

            if (ENTITY.equals(elementName))
            {
               return null;
            }

            // Create nestedMetawidget with conditional 'rendered' attribute

            StaticHtmlMetawidget nestedMetawidget = new StaticHtmlMetawidget();
            metawidget.initNestedMetawidget(nestedMetawidget, attributes);
            String unwrappedExpression = StaticFacesUtils.unwrapExpression(nestedMetawidget.getValue());
            nestedMetawidget.putAttribute("rendered",
                     StaticFacesUtils.wrapExpression("!empty " + unwrappedExpression));

            // If read-only we're done

            if (WidgetBuilderUtils.isReadOnly(attributes))
            {
               return nestedMetawidget;
            }

            // Otherwise, further wrap it with a button

            int lastIndexOf = unwrappedExpression.lastIndexOf('.');
            String parentExpression = unwrappedExpression.substring(0, lastIndexOf);
            String childExpression = unwrappedExpression.substring(lastIndexOf + 1);

            HtmlCommandLink commandLink = new HtmlCommandLink();
            commandLink.setValue("Create New " + StringUtils.uncamelCase(childExpression));
            commandLink.putAttribute(
                     "action",
                     StaticFacesUtils.wrapExpression(parentExpression + ".new"
                              + StringUtils.capitalize(childExpression)));
            commandLink.putAttribute("rendered", StaticFacesUtils.wrapExpression("empty " + unwrappedExpression));

            HtmlPanelGroup panelGroup = new HtmlPanelGroup();
            panelGroup.getChildren().add(commandLink);
            panelGroup.getChildren().add(nestedMetawidget);
            return panelGroup;
         }

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

      StandardBindingProcessor bindingProcessor = metawidget.getWidgetProcessor(StandardBindingProcessor.class);

      if (bindingProcessor != null)
      {
         bindingProcessor.processWidget(dataTable, elementName, attributes, (StaticUIMetawidget) metawidget);
      }

      ReadableIdProcessor readableIdProcessor = metawidget.getWidgetProcessor(ReadableIdProcessor.class);

      if (readableIdProcessor != null)
      {
         readableIdProcessor.processWidget(dataTable, elementName, attributes, metawidget);
      }

      ValueHolder valueHolderTable = (ValueHolder) dataTable;
      String tableValueExpression = valueHolderTable.getValue();

      // Special support for non-Lists

      Class<?> clazz = WidgetBuilderUtils.getActualClassOrType(attributes, null);

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

      // Hack until https://issues.apache.org/jira/browse/MYFACES-3410 is resolved: move the Collection into a temporary
      // variable

      HtmlPanelGroup panelGroup = new HtmlPanelGroup();

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

      // If not bidirectional, create an 'Add' section (bidirectional does it 'in place')

      if (!attributes.containsKey(INVERSE_RELATIONSHIP))
      {
         HtmlPanelGrid panelGrid = new HtmlPanelGrid();
         panelGrid.putAttribute("styleClass", "data-table-footer");
         panelGrid.putAttribute("columns", "2");
         panelGrid.putAttribute("columnClasses", ",remove-column");

         // Select menu at bottom

         HtmlSelectOneMenu select = new HtmlSelectOneMenu();
         String selectId = dataTable.getAttribute("id") + "Select";
         select.putAttribute("id", selectId);
         String requestScopedValue = "requestScope['" + selectId + "']";
         select.setValue(StaticFacesUtils.wrapExpression(requestScopedValue));
         String simpleComponentType = ClassUtils.getSimpleName(componentType);
         String controllerName = StringUtils.decapitalize(simpleComponentType);
         select.setConverter(StaticFacesUtils.wrapExpression(controllerName + "Bean.converter"));
         Map<String, String> emptyAttributes = CollectionUtils.newHashMap();
         addSelectItems(select, StaticFacesUtils.wrapExpression(controllerName + "Bean.all"), emptyAttributes);
         panelGrid.getChildren().add(select);

         // Create 'Add' button

         HtmlCommandLink addLink = new HtmlCommandLink();
         addLink.putAttribute("styleClass", "add-button");
         String addExpression = COLLECTION_VAR + ".add(" + requestScopedValue + ")";
         addLink.putAttribute("action", StaticFacesUtils.wrapExpression(addExpression));
         addLink.putAttribute("onclick", "if (document.getElementById(document.forms[0].id+':" + selectId
                  + "').selectedIndex &lt; 1) { alert('Must select a " + StringUtils.uncamelCase(simpleComponentType)
                  + "'); return false; }");

         // (id is useful for unit tests)

         addLink.putAttribute("id", dataTable.getAttribute("id") + "Add");
         panelGrid.getChildren().add(addLink);

         panelGroup.getChildren().add(panelGrid);
      }

      return panelGroup;
   }

   /**
    * Overridden to add a 'remove' column.
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
      removeLink.putAttribute("styleClass", "remove-button");
      String removeExpression = COLLECTION_VAR + ".remove(" + dataTable.getAttribute("var") + ")";
      removeLink.putAttribute("action", StaticFacesUtils.wrapExpression(removeExpression));

      HtmlColumn column = new HtmlColumn();
      column.putAttribute("headerClass", "remove-column");
      column.putAttribute("footerClass", "remove-column");
      column.getChildren().add(removeLink);
      dataTable.getChildren().add(column);

      // If bidirectional, an 'Add' button too

      String inverseRelationship = attributes.get(INVERSE_RELATIONSHIP);

      if (inverseRelationship != null)
      {
         String componentType = WidgetBuilderUtils.getComponentType(attributes);

         if (componentType != null)
         {
            String controllerName = StringUtils.decapitalize(ClassUtils.getSimpleName(componentType));

            HtmlCommandLink addLink = new HtmlCommandLink();
            addLink.putAttribute("styleClass", "add-button");
            String addExpression = COLLECTION_VAR + ".add(" + controllerName + "Bean.added)";
            addLink.putAttribute("action", StaticFacesUtils.wrapExpression(addExpression));

            // Use a f:setPropertyActionListener to initialize the bidirectional relationship

            SetPropertyActionListener setPropertyActionListener = new SetPropertyActionListener();
            setPropertyActionListener.putAttribute(
                     "target",
                     StaticFacesUtils.wrapExpression(controllerName + "Bean.add." + inverseRelationship));
            StandardBindingProcessor bindingProcessor = metawidget.getWidgetProcessor(StandardBindingProcessor.class);

            if (bindingProcessor != null)
            {
               bindingProcessor.processWidget(setPropertyActionListener, ENTITY, attributes,
                        (StaticUIMetawidget) metawidget);
            }
            addLink.getChildren().add(setPropertyActionListener);

            // (id is useful for unit tests)

            String id = StaticFacesUtils.unwrapExpression(setPropertyActionListener.getValue())
                     + StringUtils.SEPARATOR_DOT_CHAR + attributes.get(NAME) + StringUtils.SEPARATOR_DOT_CHAR + "Add";

            addLink.putAttribute("id", StringUtils.camelCase(id, StringUtils.SEPARATOR_DOT_CHAR));

            Facet footerFacet = new Facet();
            footerFacet.putAttribute("name", "footer");
            footerFacet.getChildren().add(addLink);
            column.getChildren().add(footerFacet);
         }
      }
   }

   /**
    * Overridden to replace original column text with an <tt>h:link</tt>, in those cases we can determine the dataType.
    */

   @Override
   protected void addColumnComponent(HtmlDataTable dataTable, Map<String, String> tableAttributes, String elementName,
            Map<String, String> columnAttributes,
            StaticXmlMetawidget metawidget)
   {
      // Suppress columns that show Collection values. Their toString is never very nice, and nested tables look awful!
      //
      // Note: we don't just do N_TO_MANY values, as sometimes Collections are not annotated

      Class<?> clazz = WidgetBuilderUtils.getActualClassOrType(columnAttributes, null);

      if (clazz != null && (Collection.class.isAssignableFrom(clazz)))
      {
         return;
      }

      // FORGE-446: Expand columns that show one-to-one values

      String componentType = WidgetBuilderUtils.getComponentType(tableAttributes);

      if (TRUE.equals(columnAttributes.get(ONE_TO_ONE)))
      {
         String columnType = columnAttributes.get(TYPE);
         String inspectedType = metawidget.inspect(null, columnType);

         if (inspectedType != null)
         {
            Element root = XmlUtils.documentFromString(inspectedType).getDocumentElement();
            NodeList elements = root.getFirstChild().getChildNodes();
            Map<String, String> embeddedAttributes = CollectionUtils.newHashMap();
            embeddedAttributes.put(TOP_LEVEL_PARAMETERIZED_TYPE, componentType);
            embeddedAttributes.put(PARAMETERIZED_TYPE, columnType);
            addColumnComponents(dataTable, embeddedAttributes, elements, metawidget);
            return;
         }
      }

      // FORGE-448: Don't display "owner" when showing relationships

      String columnName = columnAttributes.get(NAME);

      if (columnName.equals(tableAttributes.get(INVERSE_RELATIONSHIP)))
      {
         return;
      }

      // Create the column

      super.addColumnComponent(dataTable, tableAttributes, elementName, columnAttributes, metawidget);
      List<StaticWidget> columns = dataTable.getChildren();
      HtmlColumn column = (HtmlColumn) columns.get(columns.size() - 1);

      // If we can determine the componentType, wrap it with a link

      if (tableAttributes.get(TOP_LEVEL_PARAMETERIZED_TYPE) != null)
      {
         componentType = tableAttributes.get(TOP_LEVEL_PARAMETERIZED_TYPE);
      }

      if (componentType != null)
      {
         String controllerName = StringUtils.decapitalize(ClassUtils.getSimpleName(componentType));

         // Create a link...

         HtmlOutcomeTargetLink link = new HtmlOutcomeTargetLink();
         link.putAttribute("outcome", getTargetDir() + "/" + controllerName + "/view");

         // ...pointing to the id

         String primaryKeyName = "id";
         String inspectedType = metawidget.inspect(null, componentType);

         if (inspectedType != null)
         {
            Element root = XmlUtils.documentFromString(inspectedType).getDocumentElement();
            NodeList elements = root.getFirstChild().getChildNodes();

            for (int loop = 0, length = elements.getLength(); loop < length; loop++)
            {
               Element element = (Element) elements.item(loop);

               if (element.hasAttribute(PRIMARY_KEY))
               {
                  primaryKeyName = element.getAttribute(NAME);
                  break;
               }
            }
         }

         Param param = new Param();
         param.putAttribute("name", "id");
         param.putAttribute(
                  "value",
                  StaticFacesUtils.wrapExpression(dataTable.getAttribute("var") + StringUtils.SEPARATOR_DOT_CHAR
                           + primaryKeyName));
         link.getChildren().add(param);
         link.getChildren().add(column.getChildren().remove(1));
         column.getChildren().add(link);

         // If bidirectional, add a footer facet

         if (tableAttributes.containsKey(INVERSE_RELATIONSHIP) && !metawidget.isReadOnly())
         {
            // If it's an inverse relationship, we really should have been able to determine sub-properties, so we
            // should never be at 'entity' level *unless* componentType couldn't resolve to an actual type

            if (!ENTITY.equals(elementName))
            {
               StaticHtmlMetawidget footerMetawidget = new StaticHtmlMetawidget();
               Map<String, String> footerAttributes = CollectionUtils.newHashMap();
               metawidget.initNestedMetawidget(footerMetawidget, footerAttributes);

               // (footer facets should never have a 'required' attribute)

               footerMetawidget.removeWidgetProcessor(footerMetawidget
                        .getWidgetProcessor(RequiredAttributeProcessor.class));
               footerMetawidget.setValue(StaticFacesUtils.wrapExpression(controllerName + "Bean.add." + columnName));
               footerMetawidget.setPath(componentType + StringUtils.SEPARATOR_FORWARD_SLASH_CHAR + columnName);
               footerMetawidget.setLayout(new SimpleLayout());

               Facet footerFacet = new Facet();
               footerFacet.putAttribute("name", "footer");
               footerFacet.getChildren().add(footerMetawidget);

               ReadableIdProcessor readableIdProcessor = metawidget.getWidgetProcessor(ReadableIdProcessor.class);

               if (readableIdProcessor != null)
               {
                  readableIdProcessor.processWidget(footerMetawidget, elementName, columnAttributes, metawidget);
               }

               HtmlMessage message = new HtmlMessage();
               message.putAttribute("for", footerMetawidget.getAttribute("id"));
               message.putAttribute("styleClass", "error");
               footerFacet.getChildren().add(message);
               column.getChildren().add(footerFacet);
            }
         }
      }
   }

   //
   // Private methods
   //

   private String getTargetDir()
   {
      String target = this.config.getString(FacesScaffold.class.getName() + "_targetDir");

      target = Strings.isNullOrEmpty(target) ? "" : target;

      if (!target.startsWith("/"))
      {
         target = "/" + target;
      }
      if (target.endsWith("/"))
      {
         target = target.substring(0, target.length() - 1);
      }

      return target;
   }
}
