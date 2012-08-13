/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.project;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.NormalScope;

import org.jboss.forge.project.Project;

/**
 * Declares a bean as being scoped to the current {@link Project}. Beans using this scope will be destroyed when the
 * current {@link Project} is removed. The scope is active as long as there is an active {@link Project}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@NormalScope(passivating = false)
@Inherited
@Documented
@Target({ TYPE, METHOD, FIELD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ProjectScoped
{

}
