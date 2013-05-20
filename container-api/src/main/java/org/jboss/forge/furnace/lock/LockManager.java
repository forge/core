/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.lock;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

/**
 * An interface that provides read and write {@link Lock} instances for interacting with the underlying locking
 * mechanism.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 * @see {@link LockMode}
 */
public interface LockManager
{
   /**
    * Perform the given {@link Callable} task after acquiring a {@link Lock} of the given {@link LockMode} type. Return
    * the result, if any.
    */
   <T> T performLocked(LockMode mode, Callable<T> task);
}
