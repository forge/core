/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.impl.modules;

import org.jboss.modules.Module;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.resources.spi.ResourceLoader;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ModularWeld extends Weld
{
   private Module module;

   public ModularWeld(Module module)
   {
      this.module = module;
   }

   @Override
   protected Deployment createDeployment(final ResourceLoader resourceLoader, final Bootstrap bootstrap)
   {
      ModuleResourceLoader moduleResourceLoader = new ModuleResourceLoader(module);
      Deployment deployment = super.createDeployment(moduleResourceLoader, bootstrap);

      return deployment;
   }
}
