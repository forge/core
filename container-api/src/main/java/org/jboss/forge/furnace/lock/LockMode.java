/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.lock;

import java.util.concurrent.locks.Lock;

/**
 * A type-safe differentiation between read and write {@link Lock} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public enum LockMode
{
   /**
    * Represents a read {@link Lock}.
    */
   READ,

   /**
    * Represents a write {@link Lock}.
    */
   WRITE;
}
