/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.spec.javaee.validation;

import java.io.FileNotFoundException;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.spec.javaee.ValidationFacet;
import org.jboss.forge.spec.javaee.validation.completer.PropertyCompleter;

import static org.jboss.forge.spec.javaee.validation.util.JavaHelper.getFieldAccessor;
import static org.jboss.forge.spec.javaee.validation.util.ResourceHelper.getJavaClassFromResource;

/**
 * @author Kevin Pollet
 */
@Alias("remove-constraint")
@RequiresResource(JavaResource.class)
@RequiresFacet({ValidationFacet.class, JavaSourceFacet.class})
public class RemovePropertyConstraintPlugin implements Plugin
{
    private final Shell shell;
    private final Project project;

    @Inject
    public RemovePropertyConstraintPlugin(Shell shell, Project project)
    {
        this.shell = shell;
        this.project = project;
    }

    @DefaultCommand(help = "Removes the given constraint on the given property/property accessor")
    public void removePropertyConstraint(@Option(name = "property", completer = PropertyCompleter.class, required = true) String property,
                                         @Option(name = "named", required = true) String constraint,
                                         @Option(name = "onAccessor", flagOnly = true) boolean onAccessor) throws FileNotFoundException
    {
        final JavaClass clazz = getJavaClassFromResource(shell.getCurrentResource());
        final Member<JavaClass, ?> member = getMember(clazz, property, onAccessor);
        if (member == null)
        {
            throw new IllegalStateException("There is no property named '" + property + "' or accessor for this property");
        }

        if (!removeConstraint(member, constraint))
        {
            throw new IllegalStateException("There is no constraint named '" + constraint + "' on " + member.getName());
        }

        // save java source
        project.getFacet(JavaSourceFacet.class).saveJavaSource(clazz);
        shell.println("Constraint named '" + constraint + "' has been successfully removed.\n");
    }

    /**
     * Retrieves the property or property accessor member in the given java class.
     *
     * @param clazz      The java class.
     * @param property   The property name.
     * @param onAccessor {@code true} to retrieve the property accessor.
     * @return The targeted java member or {@code null} if none.
     */
    private Member<JavaClass, ?> getMember(JavaClass clazz, String property, boolean onAccessor)
    {
        final Field<JavaClass> field = clazz.getField(property);
        if (field != null && onAccessor)
        {
            return getFieldAccessor(field);
        }
        return field;
    }

    /**
     * Removes the given constraint on the given member.
     *
     * @param member         The member on which the constraint has to be removed.
     * @param constraint The constraint name.
     * @return {@code true} if the constraint have been successfully removed, {@code false} otherwise
     */
    private boolean removeConstraint(Member<JavaClass, ?> member, String constraint)
    {
        final List<Annotation<JavaClass>> annotations = member.getAnnotations();
        for (Annotation<JavaClass> oneAnnotation : annotations)
        {
            if (oneAnnotation.getName().equals(constraint))
            {
                member.removeAnnotation(oneAnnotation);
                return true;
            }
        }
        return false;
    }
}
