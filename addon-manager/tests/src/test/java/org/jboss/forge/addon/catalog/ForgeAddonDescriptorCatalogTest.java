/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.catalog;

import static org.hamcrest.CoreMatchers.is;

import java.util.List;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.manager.catalog.AddonDescriptor;
import org.jboss.forge.addon.manager.catalog.AddonDescriptorCatalogRegistry;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ForgeAddonDescriptorCatalogTest
{
   @Test
   public void testGetAddonDescriptors() {
      List<AddonDescriptor> addons = getAddonDescriptorCatalogRegistry().find("arquillian");
      Assert.assertThat(addons.isEmpty(), is(false));
   }

   AddonDescriptorCatalogRegistry getAddonDescriptorCatalogRegistry() {
      final AddonRegistry addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
      return addonRegistry.getServices(AddonDescriptorCatalogRegistry.class).get();
   }

}
