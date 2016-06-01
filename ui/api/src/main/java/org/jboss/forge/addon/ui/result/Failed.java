/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.result;

/**
 * Represents a failed {@link Result}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Failed extends Result
{
   /**
    * Return the {@link Exception} that caused command execution failure. (May be <code>null</code> if no
    * {@link Exception} was thrown.)
    */
   public Throwable getException();
}
