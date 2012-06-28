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

import static org.metawidget.inspector.InspectionResultConstants.*;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.*;

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
