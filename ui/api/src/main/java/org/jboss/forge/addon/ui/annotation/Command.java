/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.furnace.util.Predicate;

/**
 * Represents a single command to be run on a Shell.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Inherited
@Documented
@Retention(RUNTIME)
@Target({ METHOD })
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

   /**
    * The handler to determine if this command should be enabled
    */
   Class<? extends Predicate<UIContext>>[] enabled() default {};

   /**
    * Categories for this command
    */
   String[] categories() default {};

}
