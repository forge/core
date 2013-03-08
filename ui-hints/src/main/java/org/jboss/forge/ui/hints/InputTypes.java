/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.hints;

/**
 * The set of supported {@link InputType} hints that determine custom display options for various input types meriting
 * advanced display options or validation that would otherwise be impossible without implementation in the user
 * interface provider.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public enum InputTypes implements InputType
{
   CHECKBOX,

   TEXTBOX,
   TEXTAREA,

   FILE_PICKER,
   MULTI_FILE_PICKER,

   SELECT_ONE_DROPDOWN,
   SELECT_ONE_RADIO,
   SELECT_MANY,
   SELECT_MANY_CHECKBOX,

   SECRET,

   JAVA_CLASS_PICKER,
   MULTI_JAVA_CLASS_PICKER,

   JAVA_PACKAGE_PICKER,
   MULTI_JAVA_PACKAGE_PICKER;
}
