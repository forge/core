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

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;

/**
 * @author Kevin Pollet
 */
public final class JavaHelper
{
    private static final String GET_PREFIX = "get";
    private static final String IS_PREFIX = "is";

    // disallow instantiation of this helper class
    private JavaHelper()
    {
    }

    public static Method<JavaClass> getFieldAccessor(Field<JavaClass> field)
    {
        if (field == null)
        {
            throw new IllegalArgumentException("The given field cannot be null");
        }

        final JavaClass javaClass = field.getOrigin();
        final String accessorMethodSuffix = getAccessorMethodSuffix(field);

        Method<JavaClass> method = javaClass.getMethod(GET_PREFIX + accessorMethodSuffix);
        if (method == null && field.isType(Boolean.class))
        {
            method = javaClass.getMethod(IS_PREFIX + accessorMethodSuffix);
        }
        return method;
    }

    private static String getAccessorMethodSuffix(Field<JavaClass> field)
    {
        final String fieldName = field.getName();
        final StringBuilder methodSuffix = new StringBuilder();
        if (fieldName.length() > 0)
        {
            methodSuffix.append(Character.toUpperCase(fieldName.charAt(0)));
            if (fieldName.length() > 1)
            {
                methodSuffix.append(fieldName.substring(1, fieldName.length()));
            }
        }
        return methodSuffix.toString();
    }
}
