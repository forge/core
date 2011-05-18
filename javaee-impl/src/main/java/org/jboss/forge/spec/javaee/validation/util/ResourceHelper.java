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

import org.jboss.forge.parser.java.Annotation;
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
    //disable instantiation
    private ResourceHelper()
    {

    }

    public static boolean hasAnnotation(Resource<?> resource, Class<? extends java.lang.annotation.Annotation> annotationClass) throws FileNotFoundException
    {
        if (resource instanceof JavaResource)
        {
            final JavaClass javaClass = getJavaClassFromResource(resource);
            return javaClass.hasAnnotation(annotationClass);
        }
        else if (resource instanceof JavaMemberResource)
        {
            final JavaMemberResource javaMemberResource = (JavaMemberResource) resource;
            return javaMemberResource.getUnderlyingResourceObject().hasAnnotation(annotationClass);
        }
        throw new IllegalArgumentException("The given resource is not a Java resource");
    }

    public static Annotation<JavaClass> addAnnotationTo(Resource<?> resource, Class<? extends java.lang.annotation.Annotation> annotationClass) throws FileNotFoundException
    {
        if (resource instanceof JavaResource)
        {
            final JavaClass javaClass = getJavaClassFromResource(resource);
            return javaClass.addAnnotation(annotationClass);
        }
        else if (resource instanceof JavaMemberResource)
        {
            final JavaMemberResource javaMemberResource = (JavaMemberResource) resource;
            return javaMemberResource.getUnderlyingResourceObject().addAnnotation(annotationClass);
        }
        throw new IllegalArgumentException("An annotation can only be added on a class, field or method");
    }

    public static JavaClass getJavaClassFromResource(Resource<?> resource) throws FileNotFoundException
    {
        if (!(resource instanceof JavaResource))
        {
            throw new IllegalArgumentException("The given resource is not a java resource");
        }

        final JavaResource javaResource = (JavaResource) resource;
        final JavaSource<?> javaSource = javaResource.getJavaSource();
        if (!(javaSource.isClass() || javaSource.isInterface()))
        {
            throw new IllegalArgumentException("The given resource is not a class or an interface");
        }
        return (JavaClass) javaResource.getJavaSource();
    }
}
