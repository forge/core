/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.plugins;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.forge.resources.Resource;

/**
 * Signals to the framework that in order for the annotated element to be visible, applicable, or "in scope," the
 * current resource or any parent in its hierarchy must be of the declared {@link Resource} type.
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface RequiresResource
{
   Class<? extends Resource<?>>[] value();
}
