/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.request;

import org.jboss.forge.addon.manager.AddonInfo;

/**
 * Super interface for actions targeted at a specific addon
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface AddonActionRequest
{
   /**
    * Target Addon
    */
   AddonInfo getRequestedAddonInfo();

   /**
    * Execute the desired request
    */
   void perform();
}