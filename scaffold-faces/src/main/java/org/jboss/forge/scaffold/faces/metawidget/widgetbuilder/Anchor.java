package org.jboss.forge.scaffold.faces.metawidget.widgetbuilder;

import org.metawidget.statically.BaseStaticXmlWidget;

/**
 * Models an HTML (not JSF) &lt;a&gt; tag.
 *
 * @author Richard Kennard
 */

public class Anchor
         extends BaseStaticXmlWidget
{
   //
   // Constructor
   //

   public Anchor()
   {
      super(null, "a", null);
   }
}
