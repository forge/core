/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.api.validation;

import org.jboss.forge.ui.api.Input;

/**
 * An {@link InputViolation} is produced from an {@link InputValidator} object.
 *
 * It represents a possible input error and should be treated in the UI accordingly
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface InputViolation
{
   Input<?> getInput();

   String getErrorMessage();

   Status getStatus();

   enum Status
   {
      SEVERE, WARNING, INFO;
   }
}
