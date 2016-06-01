/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.spi.command;

import java.util.List;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.context.UIContext;

/**
 * Extension point for the 'cd' command.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface CdTokenHandler
{
   /**
    * Process the current {@link UIContext} with the given token and return the new current working {@link Resource}
    * instances, or an empty {@link List} if the token could not be handled.
    */
   public List<Resource<?>> getNewCurrentResources(UIContext current, String token);
}
