/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.io.File;

import org.jboss.aesh.console.settings.Settings;
import org.jboss.forge.addon.resource.Resource;

/**
 * Creates {@link Shell} objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ShellFactory
{
   /**
    * Create a {@link Shell} based on the specified {@link Settings}
    */
   Shell createShell(File initialSelection, Settings settings);

   /**
    * Create a {@link Shell} based on the specified {@link Settings}
    */
   Shell createShell(Resource<?> intialSelection, Settings settings);
}
