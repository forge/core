/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.modules;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.SingletonProvider;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.resources.spi.ResourceLoader;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ModularWeld extends Weld
{
   static
   {
      /*
       * This must happen once per JVM
       */
      SingletonProvider.initialize(new SilentTCCLSingletonProvider());
   }

   private ModuleScanResult scanResult;

   public ModularWeld(ModuleScanResult scanResult)
   {
      this.scanResult = scanResult;
   }

   @Override
   protected Deployment createDeployment(final ResourceLoader resourceLoader, final Bootstrap bootstrap)
   {
      return new ModularWeldDeployment(bootstrap, scanResult.getResourceLoader(), scanResult);
   }
}
