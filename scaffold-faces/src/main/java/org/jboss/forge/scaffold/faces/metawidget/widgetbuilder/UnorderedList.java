package org.jboss.forge.scaffold.faces.metawidget.widgetbuilder;

import org.metawidget.statically.BaseStaticXmlWidget;

/**
 * Models an HTML (not JSF) &lt;ul&gt; tag.
 *
 * @author Richard Kennard
 */

public class UnorderedList
         extends BaseStaticXmlWidget
{
   //
   // Constructor
   //

   public UnorderedList()
   {
      super(null, "ul", null);
   }
}
