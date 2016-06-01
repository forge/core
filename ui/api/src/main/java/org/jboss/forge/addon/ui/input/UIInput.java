/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;


/**
 * A {@link UIInput} prompts for a single value.
 *
 *
 * A {@link UICompleter} should be set when N items are provided to select from, and no specific limit or pre-defined
 * list is known.<br>
 *
 * <br>
 * When prompting for multiple values is required, see {@link UIInputMany}. <br>
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <VALUETYPE>
 */
public interface UIInput<VALUETYPE> extends InputComponent<UIInput<VALUETYPE>, VALUETYPE>,
         SingleValued<UIInput<VALUETYPE>, VALUETYPE>, HasCompleter<UIInput<VALUETYPE>, VALUETYPE>
{

}