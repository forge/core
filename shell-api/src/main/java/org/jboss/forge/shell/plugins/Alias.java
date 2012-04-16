/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.shell.plugins;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Any;
import javax.inject.Qualifier;

import org.jboss.forge.project.Facet;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.util.ConstraintInspector;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

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
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface Alias
{
   String value();
}
