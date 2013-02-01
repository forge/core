/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.proxy;

import javassist.util.proxy.MethodHandler;

/**
 * Marks an instance that was created via {@link Proxies}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ForgeProxy extends MethodHandler
{
   /**
    * Get the underlying delegate instance, if possible.
    */
   Object getDelegate();
}