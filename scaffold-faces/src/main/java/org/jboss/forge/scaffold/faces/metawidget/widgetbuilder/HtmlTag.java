package org.jboss.forge.scaffold.faces.metawidget.widgetbuilder;

import org.metawidget.statically.BaseStaticXmlWidget;

/**
 * Models an HTML (not JSF) tag.
 *
 * @author Richard Kennard
 */

public class HtmlTag
         extends BaseStaticXmlWidget
{
   //
   // Constructor
   //

   public HtmlTag(String tagName)
   {
      super(null, tagName, null);
   }
}
