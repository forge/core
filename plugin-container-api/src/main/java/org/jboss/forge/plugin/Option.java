/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A command option.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Target({ PARAMETER })
@Retention(RUNTIME)
@Documented
public @interface Option
{
   /**
    * The name of this option.
    */
   String name() default "";

   /**
    * An optional short version of the flag name.
    * 
    * @return
    */
   String shortName() default "";

   String description() default "";

   /**
    * Sets whether or not the option is just a flag. Option must be a boolean in this case.
    */
   boolean flagOnly() default false;

   /**
    * Specify whether or not this option is required.
    */
   boolean required() default false;

   /**
    * The default value for this option, if not provided in user input.
    */
   String defaultValue() default "";

   /**
    * Help text for this option.
    */
   String help() default "";

}
