/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.facets.Faceted;

/**
 * The annotated element has a relationship depending on the given {@link Facet} types.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(FacetConstraints.class)
public @interface FacetConstraint
{
   /**
    * The facets required by the annotated {@link Faceted}
    */
   @SuppressWarnings("rawtypes")
   Class<? extends Facet>[] value();

   /**
    * The type of this constraint.
    */
   FacetConstraintType type() default FacetConstraintType.REQUIRED;
}