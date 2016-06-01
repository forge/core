/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;


/**
 * A {@link UIInputMany} prompts for multiple values.
 *
 *
 * A {@link UICompleter} should be set when N items are provided to select from, and no specific limit or pre-defined
 * list is known.<br>
 *
 * <br>
 * When prompting for a single value is required, see {@link UIInput}. <br>
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <VALUETYPE>
 */
public interface UIInputMany<VALUETYPE> extends InputComponent<UIInputMany<VALUETYPE>, VALUETYPE>,
         ManyValued<UIInputMany<VALUETYPE>, VALUETYPE>, HasCompleter<UIInputMany<VALUETYPE>, VALUETYPE>
{
}