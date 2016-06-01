/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.context;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.InputComponentFactory;

/**
 * Allows {@link UICommand} objects to specify the order of the displayed fields
 * 
 * Each wizard page receives an unique instance of {@link UIBuilder}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface UIBuilder extends UIContextProvider
{
   /**
    * Implementations should call this method in order to make the {@link InputComponent} available in the UI
    * 
    * @param input
    * @return this same instance, to allow method chaining
    */
   UIBuilder add(InputComponent<?, ?> input);

   /**
    * Returns the {@link InputComponentFactory} service to allow creation of inputs ad-hoc.
    * 
    * @return {@link InputComponentFactory} service
    */
   InputComponentFactory getInputComponentFactory();
}
