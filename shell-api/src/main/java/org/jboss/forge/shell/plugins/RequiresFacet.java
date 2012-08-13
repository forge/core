/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.plugins;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.forge.project.Facet;

/**
 * The annotated element requires the given {@link Facet}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
// TODO implement handling strategy for this annotation metadata

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@RequiresProject
public @interface RequiresFacet
{
   /**
    * The {@link Facet}s required by the annotated {@link Plugin}
    */
   Class<? extends Facet>[] value();
}
