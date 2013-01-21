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
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.jboss.forge.project.Facet;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.util.ConstraintInspector;

/**
 * Most commonly, {@link Alias} is used when naming a {@link Plugin} or a {@link Facet}, but it can also be used for
 * custom implementations when combined with the {@link ConstraintInspector#getName(Class)}.
 * <p/>
 * If two or more {@link Plugin} types share an alias, they must each declare a different {@link RequiresResource}). The
 * shell determines which {@link Plugin} to invoke when a {@link Resource} of the type requested by a
 * {@link RequiresResource} is currently in scope.
 * <p/>
 * Scopes and overloads are checked at boot time; if conflicts are detected, the shell will fail to boot. (No two
 * {@link Plugin} types or commands may declare the same {@link Alias} and {@link RequiresResource}). Similarly, no two
 * {@link Facet} types may use the same {@link Alias}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Mike Brock .
 */
@Qualifier
@Documented
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface Alias
{
   String value();
}
