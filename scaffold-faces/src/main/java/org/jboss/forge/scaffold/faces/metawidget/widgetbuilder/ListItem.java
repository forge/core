package org.jboss.forge.scaffold.faces.metawidget.widgetbuilder;

import org.metawidget.statically.BaseStaticXmlWidget;

/**
 * Models an HTML (not JSF) &lt;li&gt; tag.
 *
 * @author Richard Kennard
 */

public class ListItem
         extends BaseStaticXmlWidget
{
   //
   // Constructor
   //

   public ListItem()
   {
      super(null, "li", null);
   }
}
