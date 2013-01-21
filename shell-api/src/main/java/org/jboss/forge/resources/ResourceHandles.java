/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resources;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Declares a resource handler for specified wildcards. For example: <tt><code>
 * 
 * @ResourceHandles({"*.txt", "*.text", "README"}) public class TextResource extends Resource { ... } </code></tt>
 * 
 * @author Mike Brock <cbrock@redhat.com>
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface ResourceHandles
{
   String[] value();
}
