/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.facets;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for {@link HintsFacet}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class HintsFacetTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML();
   }

   @Inject
   private UIInput<String> input;

   @Test
   public void testPromptInInteractiveMode() throws Exception
   {
      Assert.assertTrue(input.hasFacet(HintsFacet.class));
      HintsFacet facet = input.getFacet(HintsFacet.class);
      Assert.assertFalse(facet.isPromptInInteractiveMode());

      // By default, the UI should prompt if required and no value is set
      input.setRequired(true);
      Assert.assertTrue(facet.isPromptInInteractiveMode());

      // Checking if initial state is still ok
      input.setRequired(false);
      Assert.assertFalse(facet.isPromptInInteractiveMode());

      // Should not prompt if explicitly set
      input.setRequired(true);
      facet.setPromptInInteractiveMode(false);
      Assert.assertFalse(facet.isPromptInInteractiveMode());

      // Using the Callable version
      facet.setPromptInInteractiveMode(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return true;
         }
      });
      Assert.assertTrue(facet.isPromptInInteractiveMode());
   }
}
