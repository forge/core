/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.bus;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@ApplicationScoped
public class MockEventObserver
{
   private int count = 0;
   private int countSpecial = 0;

   public void observe(@Observes final MockEvent event)
   {
      count++;
   }

   public void observeSpecial(@Observes @Special final MockEvent event)
   {
      countSpecial++;
   }

   public int getCount()
   {
      return count;
   }

   public int getCountSpecial()
   {
      return countSpecial;
   }
}
