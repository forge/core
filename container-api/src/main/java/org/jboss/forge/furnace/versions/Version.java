/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.versions;

import org.jboss.forge.furnace.addons.Addon;

/**
 * Represents an {@link Addon} version.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Version
{
   /**
    * Get the {@link String} representation of this {@link Version} instance.
    */
   String getVersionString();
}
