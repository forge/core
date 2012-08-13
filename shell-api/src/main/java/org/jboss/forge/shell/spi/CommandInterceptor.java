/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.spi;

/**
 * Allows the shell input stream to be intercepted, modified, or observed by extensions.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface CommandInterceptor
{
   /**
    * Intercepts a command and returns it along with any modifications. The returned value will be passed to the Shell
    * for invocation. If the returned value is null, the shell simply ignores this line and waits for the next input.
    */
   public String intercept(String line);
}
