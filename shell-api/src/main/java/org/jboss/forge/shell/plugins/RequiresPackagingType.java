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
import org.jboss.forge.project.packaging.PackagingType;

/**
 * The annotated element requires the given {@link PackagingType}s
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@RequiresProject
public @interface RequiresPackagingType
{
   /**
    * The array of {@link PackagingType}s required by the annotated {@link Facet} or {@link Plugin}
    */
   PackagingType[] value();
}
