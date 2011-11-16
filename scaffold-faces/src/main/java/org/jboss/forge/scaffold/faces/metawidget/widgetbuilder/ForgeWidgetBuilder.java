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

import java.util.Map;

import org.metawidget.statically.StaticMetawidget;
import org.metawidget.statically.StaticXmlWidget;
import org.metawidget.statically.faces.StaticFacesUtils;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlLink;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlWidgetBuilder;
import org.metawidget.statically.faces.component.html.widgetbuilder.Param;

/**
 * @author Richard Kennard
 */

public class ForgeWidgetBuilder
         extends HtmlWidgetBuilder
{
   //
   // Protected methods
   //

   /**
    * Overriden to wrap column text with an <tt>h:link</tt>.
    */

   @Override
   protected StaticXmlWidget createColumnComponent(String dataTableVar, String elementName,
            Map<String, String> attributes, StaticMetawidget metawidget)
   {
      StaticXmlWidget column = super.createColumnComponent(dataTableVar, elementName, attributes, metawidget);

      HtmlLink link = new HtmlLink();
      link.putAttribute("outcome", "view");

      Param param = new Param();
      param.putAttribute("id", StaticFacesUtils.wrapExpression(dataTableVar + ".id"));
      link.getChildren().add(param);

      link.getChildren().add(column.getChildren().remove(1));
      column.getChildren().add(link);

      return column;
   }
}
