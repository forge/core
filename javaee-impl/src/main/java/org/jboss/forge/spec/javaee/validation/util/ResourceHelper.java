/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
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
