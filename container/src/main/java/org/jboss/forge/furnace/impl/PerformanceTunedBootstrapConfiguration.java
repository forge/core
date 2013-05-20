/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.forge.furnace.impl;

import org.jboss.weld.bootstrap.spi.BootstrapConfiguration;
import org.jboss.weld.resources.spi.ResourceLoader;

public class PerformanceTunedBootstrapConfiguration implements BootstrapConfiguration
{
   public PerformanceTunedBootstrapConfiguration(ResourceLoader loader)
   {
      // TODO detect # of CPUs and #of classes to scan. optimize pools respectively
   }

   @Override
   public boolean isConcurrentDeploymentEnabled()
   {
      return false;
   }

   @Override
   public int getPreloaderThreadPoolSize()
   {
      return 0;
      // return Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
   }

   @Override
   public void cleanup()
   {
   }
}
