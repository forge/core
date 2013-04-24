/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container;

import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.spi.ContainerLifecycleListener;

public class TestLifecycleListener implements ContainerLifecycleListener
{
   public int beforeStartTimesCalled;
   public int beforeStopTimesCalled;
   public int afterStopTimesCalled;

   @Override
   public void beforeStart(Forge forge) throws ContainerException
   {
      beforeStartTimesCalled++;
   }

   @Override
   public void beforeStop(Forge forge) throws ContainerException
   {
      beforeStopTimesCalled++;
   }

   @Override
   public void afterStop(Forge forge) throws ContainerException
   {
      afterStopTimesCalled++;
   }
}
