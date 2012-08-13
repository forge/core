/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.bus;

import java.util.Collections;
import java.util.List;

/**
 * An exception queued and re-thrown during the event bus firing process.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class EventBusQueuedException extends RuntimeException
{
   private static final long serialVersionUID = -2585821467963985204L;
   private final List<Exception> queuedExceptions;

   EventBusQueuedException(final List<Exception> thrown)
   {
      super("Aggregated [" + thrown.size() + "] caught exceptions during event bus firing.");
      queuedExceptions = thrown;
   }

   public List<Exception> getQueuedExceptions()
   {
      return Collections.unmodifiableList(queuedExceptions);
   }

   // TODO override Exception methods to provide aggregate trace reporting
}
