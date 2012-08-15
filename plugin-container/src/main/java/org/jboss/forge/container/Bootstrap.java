/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Bootstrap
{
   private static Thread currentContainer;

   @Inject
   private BeanManager manager;

   private static void init()
   {
      /*
       * One classloader/weld container per plugin module. One primary executor container running, fires events to each
       * plugin-container.
       * 
       * Multi-threaded bootstrap. Loads primary container, then attaches individual plugin containers as they come up.
       * 
       * Prevents weld library conflicts.
       */
   }
}
