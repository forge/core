/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.environment;

import org.jboss.forge.addon.environment.Category;
import org.jboss.forge.addon.environment.Environment;

/**
 * Represents the Maven network status {@link Environment} {@link Category}.
 * <p>
 * TODO Move this to a separate "network" addon
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Network implements Category
{
   private static final Object OFFLINE = Network.class.getName() + "_OFFLINE";

   /**
    * Returns <code>true</code> if the system is currently OFFLINE.
    */
   public static boolean isOffline(Environment environment)
   {
      return environment.get(Network.class).containsKey(OFFLINE);
   }
}
