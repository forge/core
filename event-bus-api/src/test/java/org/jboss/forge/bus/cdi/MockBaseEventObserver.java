/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.bus.cdi;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.jboss.forge.bus.MockEvent;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class MockBaseEventObserver
{
   private boolean observedRemoved = false;
   private boolean observedRemoved2 = false;
   private boolean observedNormal = false;

   public void managedObserver(@Observes final MockEvent event)
   {
      observedRemoved = true;
   }

   public void managedObserver2(@Observes final MockEvent event)
   {
      observedRemoved2 = true;
   }

   public void activeObserver(@Observes final MockNormalEvent event)
   {
      observedNormal = true;
   }

   public boolean hasObservedRemoved()
   {
      return observedRemoved;
   }

   public void setObservedRemoved(final boolean observed)
   {
      this.observedRemoved = observed;
   }

   public boolean hasObservedNormal()
   {
      return observedNormal;
   }

   public void setObservedNormal(final boolean observedNormal)
   {
      this.observedNormal = observedNormal;
   }

   public boolean hasObservedRemoved2()
   {
      return observedRemoved2;
   }

   public void setObservedRemoved2(final boolean observedRemoved2)
   {
      this.observedRemoved2 = observedRemoved2;
   }

}
