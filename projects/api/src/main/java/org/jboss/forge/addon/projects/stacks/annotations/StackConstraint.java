/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.stacks.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.forge.addon.facets.Faceted;
import org.jboss.forge.addon.projects.ProjectFacet;

/**
 * The command annotated with this annotation requires the current project stack to support the given facets
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Repeatable(StackConstraints.class)
public @interface StackConstraint
{
   /**
    * The facets required by the annotated {@link Faceted}
    */
   Class<? extends ProjectFacet>[] value();
}