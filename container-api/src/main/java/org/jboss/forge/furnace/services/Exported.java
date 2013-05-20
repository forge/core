/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.services;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;

/**
 * Specifies that the target type should be exported to dependent containers, and that it may be imported via
 * {@link Inject} annotation.
 *
 * ATTENTION: You MUST add @Exported to all the injectable types (including any interfaces).
 *
 * If any subtype isn't meant to be published, please add a {@link Vetoed} annotation to your subtype.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface Exported
{
}
