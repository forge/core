/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.example.extension;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.inject.Singleton;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class TestExtension implements Extension
{
   private boolean invoked = false;

   public void processRemotes(@Observes ProcessAnnotatedType<?> event)
   {
      if (!invoked)
         invoked = true;
   }

   public boolean isInvoked()
   {
      return invoked;
   }
}
