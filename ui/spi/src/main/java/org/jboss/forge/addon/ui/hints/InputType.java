/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
public enum InputType
{
   DEFAULT,

   CHECKBOX,

   TEXTBOX,
   TEXTAREA,

   FILE_PICKER,

   SELECT_ONE_DROPDOWN,
   SELECT_ONE_RADIO,
   SELECT_MANY,
   SELECT_MANY_CHECKBOX,

   SECRET,

   JAVA_CLASS_PICKER,

   JAVA_PACKAGE_PICKER;
}
