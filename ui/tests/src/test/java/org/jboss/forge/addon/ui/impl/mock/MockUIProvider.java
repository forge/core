/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.mock;

import org.jboss.forge.addon.ui.DefaultUIDesktop;
import org.jboss.forge.addon.ui.UIDesktop;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.output.UIOutput;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class MockUIProvider implements UIProvider
{
   private boolean graphical;
   private final UIOutput output;

   public MockUIProvider(boolean graphical)
   {
      this.graphical = graphical;
      this.output = new UIOutputImpl(System.out, System.err);
   }

   @Override
   public String getName()
   {
      return "Mock";
   }

   @Override
   public boolean isGUI()
   {
      return graphical;
   }

   public void setGUI(boolean graphical)
   {
      this.graphical = graphical;
   }

   @Override
   public UIOutput getOutput()
   {
      return output;
   }

   @Override
   public UIDesktop getDesktop()
   {
      return new DefaultUIDesktop();
   }

   @Override
   public boolean isEmbedded()
   {
      return false;
   }
}
