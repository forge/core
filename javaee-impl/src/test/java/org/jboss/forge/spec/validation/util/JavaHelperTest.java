/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.validation.util;

import static org.jboss.forge.parser.JavaParser.create;
import static org.jboss.forge.spec.javaee.validation.util.JavaHelper.getFieldAccessor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.junit.Test;

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
