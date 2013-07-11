/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.impl.request;

import org.jboss.forge.addon.manager.AddonInfo;
import org.jboss.forge.addon.manager.request.EnableRequest;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;

/**
 * Enable an addon
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
class EnableRequestImpl extends AbstractAddonActionRequest implements EnableRequest
{
   EnableRequestImpl(AddonInfo info, MutableAddonRepository repository, Furnace forge)
   {
      super(info, repository, forge);
   }

   @Override
   public void execute()
   {
      AddonId id = getRequestedAddonInfo().getAddon();
      repository.enable(id);
   }
}
