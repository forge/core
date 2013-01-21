/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.plugins;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Inject;
import javax.inject.Qualifier;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.project.ResourceScoped;

/**
 * Used to @{@link Inject} the current working {@link Resource}, e.g:
 * <p/>
 * <code> @Inject @Current JavaResource resource; </code>
 * <p/>
 * Will inject the current resource if and only if it is a Java Resource; otherwise, the injected value will be null.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Qualifier
@Documented
@ResourceScoped
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE, METHOD, FIELD, PARAMETER })
public @interface Current
{

}
