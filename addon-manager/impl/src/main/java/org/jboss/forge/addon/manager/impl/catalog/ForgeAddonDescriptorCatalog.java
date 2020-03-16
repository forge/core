/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl.catalog;

import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.forge.addon.manager.catalog.URLAddonDescriptorCatalog;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeAddonDescriptorCatalog extends URLAddonDescriptorCatalog {

   public ForgeAddonDescriptorCatalog() throws MalformedURLException {
      super(new URL("https://forge.jboss.org/api/addons?source=cmd"));
   }

   @Override
   public String getName() {
      return "forge";
   }

}
