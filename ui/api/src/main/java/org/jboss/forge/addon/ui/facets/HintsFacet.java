/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.facets;

import java.util.concurrent.Callable;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * Extends the {@link InputComponent} behavior with some orthogonal features (hints)
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface HintsFacet extends Facet<InputComponent<?, ?>>
{
   /**
    * @return the input type as defined in {@link InputType}
    */
   String getInputType();

   /**
    * @param type the input type as defined in {@link InputType}
    * @return this {@link HintsFacet} instance for method chaining purposes
    */
   HintsFacet setInputType(String type);

   /**
    * @return true if the associated {@link InputComponent} should prompt values when in CLI interactive mode regardless
    *         if it's required or not
    */
   boolean isPromptInInteractiveMode();

   /**
    * Sets the associated {@link InputComponent} to prompt for values when in CLI interactive mode
    * 
    * @param prompt if this {@link InputComponent} should prompt for values regardless if it's required or not
    * @return this {@link HintsFacet} instance for method chaining purposes
    */
   HintsFacet setPromptInInteractiveMode(boolean prompt);

   /**
    * Sets the associated {@link InputComponent} to prompt for values when in CLI interactive mode
    * 
    * @param prompt the {@link Callable} returning a {@link Boolean} if this {@link InputComponent} should prompt for
    *           values regardless if it's required or not
    * @return this {@link HintsFacet} instance for method chaining purposes
    */
   HintsFacet setPromptInInteractiveMode(Callable<Boolean> prompt);
}