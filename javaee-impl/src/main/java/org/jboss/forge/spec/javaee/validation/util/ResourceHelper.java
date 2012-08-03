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
package org.jboss.forge.spec.javaee.validation.util;

import java.io.FileNotFoundException;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaMemberResource;
import org.jboss.forge.resources.java.JavaResource;

/**
 * @author Kevin Pollet
 */
public final class ResourceHelper
{
    //disallow instantiation
    private ResourceHelper()
    {

    }

    @SuppressWarnings("unchecked")
   public static boolean hasAnnotation(Resource<?> resource, Class<? extends java.lang.annotation.Annotation> annotationClass) throws FileNotFoundException
    {
        if (resource == null)
        {
            throw new IllegalArgumentException("The parameter 'resource' cannot be null");
        }
        if (annotationClass == null)
        {
            throw new IllegalArgumentException("The parameter 'annotationClass' cannot be null");
        }

        if (resource instanceof JavaResource)
        {
            final JavaClass javaClass = getJavaClassFromResource(resource);
            return javaClass.hasAnnotation(annotationClass);
        }
        else if (resource instanceof JavaMemberResource)
        {
            final JavaMemberResource<?> javaMemberResource = (JavaMemberResource<?>) resource;
            return javaMemberResource.getUnderlyingResourceObject().hasAnnotation(annotationClass);
        }
        throw new IllegalArgumentException("The given resource '" + resource.getName() + "' is not a Java resource");
    }

    public static JavaClass getJavaClassFromResource(Resource<?> resource) throws FileNotFoundException
    {
        if (resource == null)
        {
            throw new IllegalArgumentException("The parameter 'resource' cannot be null");
        }
        if (!(resource instanceof JavaResource))
        {
            throw new IllegalArgumentException("The given resource '" + resource.getName() + "' is not a Java resource");
        }

        final JavaResource javaResource = (JavaResource) resource;
        final JavaSource<?> javaSource = javaResource.getJavaSource();
        if (!(javaSource.isClass() || javaSource.isInterface()))
        {
            throw new IllegalArgumentException("The given resource '" + resource.getName() + "' is not a class or an interface");
        }
        return (JavaClass) javaResource.getJavaSource();
    }
}
