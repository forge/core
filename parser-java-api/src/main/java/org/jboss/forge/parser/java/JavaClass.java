/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.parser.java;

import java.util.List;

import org.jboss.forge.parser.JavaParser;

/**
 * Represents a Java {@link Class} or interface source file as an in-memory modifiable element. See {@link JavaParser}
 * for various options in generating {@link JavaClass} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface JavaClass extends
         JavaType<JavaClass>,
         Extendable<JavaClass>,
         Abstractable<JavaClass>
{
   /**
    * Return a list containing {@link JavaSource} instances for each nested {@link Class} declaration found within
    * <code>this</code>. Any modification of returned {@link JavaSource} instances will result in modification of the
    * contents contained by <code>this</code> the parent instance.
    */
   List<JavaSource<?>> getNestedClasses();

}