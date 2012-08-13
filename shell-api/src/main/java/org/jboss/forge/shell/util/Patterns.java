/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.util;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface Patterns
{
   /**
    * A group of terms that are illegal to use as Java identifiers. For example: "(if|package|public...while)" and so
    * on.
    */
   public static final String JAVA_KEYWORDS = "abstract|" +
            "continue|" + "for|" + "new|" + "switch|" + "assert|" + "default|" +
            "if|" + "package|" + "synchronized|" + "boolean|" + "do|" + "goto|" +
            "private|" + "this|" + "break|" + "double|" + "implements|" + "protected|" +
            "throw|" + "byte|" + "else|" + "import|" + "public|" + "throws|" +
            "case|" + "enum|" + "instanceof|" + "return|" + "transient|" + "catch|" +
            "extends|" + "int|" + "short|" + "try|" + "char|" + "final|" +
            "interface|" + "static|" + "void|" + "class|" + "finally|" + "long|" +
            "strictfp|" + "volatile|" + "const|" + "float|" + "native|" + "super|" +
            "while";
}
