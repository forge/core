/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

import org.jboss.forge.addon.ui.context.UIContext;

/**
 * Adds an auto-complete behavior on fields
 * 
 * @param <VALUETYPE> The value type to be provided by completion.
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface UICompleter<VALUETYPE>
{
   /**
    * Get completion proposals for the provided {@link UIInput} and un-converted partial {@link String} value.
    * 
    * @param context The {@link UIContext} used in this interaction
    * @param input The {@link UIInput} currently being completed.
    * @param value The user input value requiring completion, or null, if no value yet exists. These values will undergo
    *           conversion to fit the type required by the corresponding {@link UIInput}.
    */
   Iterable<VALUETYPE> getCompletionProposals(UIContext context, InputComponent<?, VALUETYPE> input, String value);
}
