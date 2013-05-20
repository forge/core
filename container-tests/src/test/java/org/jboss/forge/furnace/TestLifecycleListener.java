/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.furnace;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.spi.ContainerLifecycleListener;

public class TestLifecycleListener implements ContainerLifecycleListener
{
   public int beforeStartTimesCalled;
   public int beforeStopTimesCalled;
   public int afterStopTimesCalled;

   @Override
   public void beforeStart(Furnace forge) throws ContainerException
   {
      beforeStartTimesCalled++;
   }

   @Override
   public void beforeStop(Furnace forge) throws ContainerException
   {
      beforeStopTimesCalled++;
   }

   @Override
   public void afterStop(Furnace forge) throws ContainerException
   {
      afterStopTimesCalled++;
   }
}
