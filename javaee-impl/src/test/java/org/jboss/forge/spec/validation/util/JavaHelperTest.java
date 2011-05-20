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
package org.jboss.forge.spec.validation.util;

import java.io.IOException;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.jboss.forge.parser.JavaParser.create;
import static org.jboss.forge.spec.javaee.validation.util.JavaHelper.getFieldAccessor;

/**
 * @author Kevin Pollet
 */
public class JavaHelperTest
{
    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveJavaGetterOfNullProperty()
    {
        getFieldAccessor(null);
    }

    @Test
    public void testRetrieveIsAccessorOfBooleanProperty() throws IOException
    {
        final JavaClass fooClass = create(JavaClass.class)
                .setPackage("com.test")
                .setName("Foo");

        final Field<JavaClass> primitiveProperty = fooClass.addField("private boolean foo;");
        fooClass.addMethod("public boolean isFoo(){return foo;}");
        fooClass.addMethod("public boolean getFoo(){return foo;}");

        final Field<JavaClass> wrapperProperty = fooClass.addField("private Boolean bar;");
        fooClass.addMethod("public Boolean isBar(){return bar;}");
        fooClass.addMethod("public Boolean getBar(){return bar;}");

        final Method<JavaClass> primitiveAccessor = getFieldAccessor(primitiveProperty);

        assertNotNull(primitiveAccessor);
        assertEquals("isFoo", primitiveAccessor.getName());

        final Method<JavaClass> wrapperAccessor = getFieldAccessor(wrapperProperty);

        assertNotNull(wrapperAccessor);
        assertEquals("isBar", wrapperAccessor.getName());
    }

    @Test
    public void testRetrieveGetAccessorOfBooleanProperty() throws IOException
    {
        final JavaClass fooClass = create(JavaClass.class)
                .setPackage("com.test")
                .setName("Foo");

        final Field<JavaClass> property = fooClass.addField("private boolean foo;");
        fooClass.addMethod("public boolean getFoo(){return foo;}");

        final Method<JavaClass> getter = getFieldAccessor(property);

        assertNotNull(getter);
        assertEquals("getFoo", getter.getName());
    }

    @Test
    public void testRetrieveGetAccessorOfProperty() throws IOException
    {
        final JavaClass fooClass = create(JavaClass.class)
                .setPackage("com.test")
                .setName("Foo");

        final Field<JavaClass> property = fooClass.addField("private int foo;");
        fooClass.addMethod("public int getFoo(){return foo;}");

        final Method<JavaClass> getter = getFieldAccessor(property);

        assertNotNull(getter);
        assertEquals("getFoo", getter.getName());
    }
}
