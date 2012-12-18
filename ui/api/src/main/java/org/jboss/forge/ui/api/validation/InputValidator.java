/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.api.validation;

import java.util.Collection;

import org.jboss.forge.ui.api.Input;

/**
 * Validates input
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface InputValidator<T>
{
   /**
    * It should validate the input and if any error is found, add an error message to the errors collection
    *
    * @param input
    * @param errors
    */
   void validate(Input<T> input, Collection<InputViolation> errors);
}
