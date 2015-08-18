/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl.utils;

import java.io.File;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Predicate;

/**
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class DistributionDirectoryExistsPredicate implements Predicate<UIContext>
{
   @Override
   public boolean accept(UIContext type)
   {
      File forgeHomeDir = OperatingSystemUtils.getForgeHomeDir();
      if (forgeHomeDir == null)
         return false;
      File updateDir = new File(forgeHomeDir, ".update");
      return updateDir.exists();
   }

}
