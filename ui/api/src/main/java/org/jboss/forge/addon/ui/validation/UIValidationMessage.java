/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.validation;

import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * A validation message
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface UIValidationMessage
{
   String getDescription();

   Severity getSeverity();

   /**
    * Returns the input component that was referenced by this message (Can be null.)
    * 
    * @return null if there is no {@link InputComponent} associated with this message
    */
   InputComponent<?, ?> getSource();

   enum Severity
   {
      INFO, ERROR, WARN;
   }
}
