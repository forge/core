/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.util;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonFilter;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonFilters
{
   public static AddonFilter allLoaded()
   {
      return new AddonFilter()
      {
         @Override
         public boolean accept(Addon addon)
         {
            return addon.getStatus().isLoaded();
         }
      };
   }

   public static AddonFilter allStarting()
   {
      return new AddonFilter()
      {
         @Override
         public boolean accept(Addon addon)
         {
            try
            {
               Future<Void> future = addon.getFuture();
               future.get(0, TimeUnit.MILLISECONDS);
            }
            catch (TimeoutException e)
            {
               return true;
            }
            catch (Exception dontCare)
            {
            }
            return false;
         }
      };
   }

   public static AddonFilter allStarted()
   {
      return new AddonFilter()
      {
         @Override
         public boolean accept(Addon addon)
         {
            return addon.getStatus().isStarted();
         }
      };
   }

   public static AddonFilter allNotStarted()
   {
      return new AddonFilter()
      {
         @Override
         public boolean accept(Addon addon)
         {
            return !addon.getStatus().isStarted();
         }
      };
   }

   public static AddonFilter all()
   {
      return new AddonFilter()
      {
         @Override
         public boolean accept(Addon addon)
         {
            return true;
         }
      };
   }

}
