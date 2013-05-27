/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.ui.test.impl.UIContextImpl;
import org.jboss.forge.ui.test.impl.WizardTesterImpl;

/**
 * A factory for {@link WizardTester} objects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class WizardTesterFactory
{

   // @Produces
   // public <W extends UIWizard> WizardTester<W> produceWizardTester(InjectionPoint injectionPoint)
   // {
   // return null;
   // }

   public static <W extends UIWizard> WizardTesterImpl<W> create(Class<W> wizardClass, AddonRegistry addonRegistry,
            Resource<?>... initialSelection) throws Exception
   {
      UIContextImpl context = new UIContextImpl(initialSelection);
      return new WizardTesterImpl<W>(wizardClass, addonRegistry, context);
   }
}
