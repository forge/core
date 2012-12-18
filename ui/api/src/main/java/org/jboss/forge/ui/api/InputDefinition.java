/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.api;

import java.util.List;

import org.jboss.forge.ui.api.validation.InputValidator;

/**
 * Defines how an input should be informed.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <T>
 */
public interface InputDefinition<T> extends InputState, Defaultable<T>
{
   String getName();

   Class<T> getType();

   List<InputValidator<T>> getRules();

   /**
    * {@link Input} objects are created through an {@link InputDefinition}
    *
    * @param value
    * @return
    */
   Input<T> createInputFor(T value);
}
