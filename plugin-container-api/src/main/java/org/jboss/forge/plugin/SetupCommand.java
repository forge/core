/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines a @{@link Command} to be used when performing setup for a {@link Plugin}. It will be run when using the
 * "setup ****" command from the shell. There may be only one {@link SetupCommand} per {@link Plugin}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Command("setup")
@Target({ METHOD })
@Retention(RUNTIME)
@Documented
public @interface SetupCommand
{
   /**
    * Help text for the setup command.
    */
   String help() default "Install and/or set up this plugin";
}
