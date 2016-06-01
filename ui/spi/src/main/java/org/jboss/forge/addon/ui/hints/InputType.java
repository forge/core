/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.hints;

/**
 * The set of supported {@link InputType} hints that determine custom display options for various input types meriting
 * advanced display options or validation that would otherwise be impossible without implementation in the user
 * interface provider.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public final class InputType
{
   /**
    * Not instantiable
    */
   private InputType()
   {
   }

   private static final String PREFIX = "org.jboss.forge.inputType.";

   public static final String DEFAULT = PREFIX + "DEFAULT";
   public static final String CHECKBOX = PREFIX + "CHECKBOX";
   public static final String TEXTBOX = PREFIX + "TEXTBOX";
   public static final String TEXTAREA = PREFIX + "TEXTAREA";
   public static final String FILE_PICKER = PREFIX + "FILE_PICKER";
   public static final String DIRECTORY_PICKER = PREFIX + "DIRECTORY_PICKER";
   public static final String DROPDOWN = PREFIX + "DROPDOWN";
   public static final String RADIO = PREFIX + "RADIO";
   public static final String SECRET = PREFIX + "SECRET";
   public static final String JAVA_CLASS_PICKER = PREFIX + "JAVA_CLASS_PICKER";
   public static final String JAVA_PACKAGE_PICKER = PREFIX + "JAVA_PACKAGE_PICKER";
}
