/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.forge.ui.input.InputComponent;

/**
 * Allows configuration of {@link InputComponent} injected fields
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface WithAttributes
{
   /**
    * The label of this input
    */
   String label();

   /**
    * Is this input required?
    */
   boolean required() default false;

   /**
    * The required message for this input
    */
   String requiredMessage() default "";

   /**
    * Is this input enabled?
    */
   boolean enabled() default true;
}
