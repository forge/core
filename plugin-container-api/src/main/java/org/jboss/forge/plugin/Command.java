/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents a single command to be run on a Shell.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Inherited
@Qualifier
@Documented
@Retention(RUNTIME)
@Target({ METHOD, PARAMETER, TYPE, FIELD })
public @interface Command
{
   /**
    * One or more names for this command.
    */
   String value() default "";

   /**
    * Help text for this command.
    */
   String help() default "";
}
