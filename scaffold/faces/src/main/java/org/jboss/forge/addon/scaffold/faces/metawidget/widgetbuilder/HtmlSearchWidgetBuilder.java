/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces.metawidget.widgetbuilder;

import static org.metawidget.inspector.InspectionResultConstants.LOOKUP;
import static org.metawidget.inspector.InspectionResultConstants.LOOKUP_LABELS;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.FACES_LOOKUP;

import java.util.List;
import java.util.Map;

import org.metawidget.statically.StaticXmlMetawidget;
import org.metawidget.statically.StaticXmlWidget;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlSelectOneMenu;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlWidgetBuilder;
import org.metawidget.statically.faces.component.html.widgetbuilder.SelectItem;
import org.metawidget.statically.faces.component.html.widgetbuilder.SelectItems;
import org.metawidget.util.CollectionUtils;

/**
 * @author <a href="mailto:ryan.k.bradley@gmail.com">Ryan Bradley</a>
 */

public class HtmlSearchWidgetBuilder extends HtmlWidgetBuilder
{
   @Override
   public StaticXmlWidget buildWidget(String elementName, Map<String, String> attributes, StaticXmlMetawidget metawidget)
   {
      // Faces lookups

      String facesLookup = attributes.get(FACES_LOOKUP);

      if (facesLookup != null && !"".equals(facesLookup))
      {
         HtmlSelectOneMenu select = new HtmlSelectOneMenu();
         addSelectItems(select, facesLookup, attributes);
         return select;
      }

      // Lookups

      String lookup = attributes.get(LOOKUP);

      if (lookup != null && !"".equals(lookup))
      {
         HtmlSelectOneMenu select = new HtmlSelectOneMenu();
         addSelectItems(select, CollectionUtils.fromString(lookup),
                  CollectionUtils.fromString(attributes.get(LOOKUP_LABELS)));
         return select;
      }

      return super.buildWidget(elementName, attributes, metawidget);
   }

   @Override
   protected void addSelectItems(HtmlSelectOneMenu select, String valueExpression, Map<String, String> attributes)
   {
      // Add an empty option to select menus on search pages

      addSelectItem(select, null, null);

      SelectItems items = new SelectItems();
      items.putAttribute("value", valueExpression);

      select.getChildren().add(items);
   }

   private void addSelectItems(HtmlSelectOneMenu select, List<String> values, List<String> labels)
   {
      if (values == null)
      {
         return;
      }

      // Add an empty option, regardless of whether it is REQUIRED

      addSelectItem(select, null, null);

      // Add the select items

      for (int loop = 0; loop < values.size(); loop++)
      {
         String value = values.get(loop);
         String label = null;

         if (labels != null && !labels.isEmpty())
         {
            label = labels.get(loop);
         }

         addSelectItem(select, value, label);
      }
   }

   private void addSelectItem(HtmlSelectOneMenu select, String value, String label)
   {
      SelectItem item = new SelectItem();
      item.putAttribute("itemValue", value);
      item.putAttribute("itemLabel", label);

      select.getChildren().add(item);
   }
}
