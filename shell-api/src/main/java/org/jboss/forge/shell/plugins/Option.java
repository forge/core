/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.plugins;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.completer.CommandCompleter;
import org.jboss.forge.shell.completer.NullCommandCompleter;

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

   /**
    * The prompt type to use when validating user input. This should be used carefully!
    * <p/>
    * <b>**WARNING**</b> Since specifying a {@link PromptType} restricts user input, you need to make sure that the
    * option type is compatible with this input, or exceptions may occur. (String or Object are your safest choices.)
    */
   PromptType type() default PromptType.ANY;

   /**
    * Specify the command completer type that should be used for this option. This may only be used with named options.
    */
   Class<? extends CommandCompleter> completer() default NullCommandCompleter.class;
}
