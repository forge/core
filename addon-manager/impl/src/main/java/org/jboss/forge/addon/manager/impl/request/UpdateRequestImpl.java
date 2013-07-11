/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl.request;

import java.util.Arrays;

import org.jboss.forge.addon.manager.AddonInfo;
import org.jboss.forge.addon.manager.request.DeployRequest;
import org.jboss.forge.addon.manager.request.RemoveRequest;
import org.jboss.forge.addon.manager.request.UpdateRequest;

/**
 * An update consists in a two-step process: Remove the original addon and install the new one
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
class UpdateRequestImpl implements UpdateRequest
{
   private final RemoveRequest removeRequest;
   private final DeployRequest deployRequest;

   public UpdateRequestImpl(RemoveRequest removeRequest, DeployRequest deployRequest)
   {
      this.removeRequest = removeRequest;
      this.deployRequest = deployRequest;
   }

   @Override
   public AddonInfo getRequestedAddonInfo()
   {
      return deployRequest.getRequestedAddonInfo();
   }

   @Override
   public DeployRequest getDeployRequest()
   {
      return deployRequest;
   }

   @Override
   public RemoveRequest getRemoveRequest()
   {
      return removeRequest;
   }

   @Override
   public void perform()
   {
      removeRequest.perform();
      deployRequest.perform();
   }

   @Override
   public String toString()
   {
      return Arrays.toString(new Object[] { removeRequest, deployRequest });
   }
}
