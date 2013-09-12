/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.ui;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.furnace.util.Assert;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellValidationContext implements UIValidationContext
{
   private final ShellContext shellContext;
   private List<String> errors = new ArrayList<String>();
   private List<String> warnings = new ArrayList<String>();

   public ShellValidationContext(ShellContext shellContext)
   {
      this.shellContext = shellContext;
   }

   @Override
   public UIContext getUIContext()
   {
      return shellContext;
   }

   @Override
   public void addValidationError(InputComponent<?, ?> input, String errorMessage)
   {
      Assert.notNull(errorMessage, "Error message should not be null");
      errors.add(errorMessage);
   }

   @Override
   public void addValidationWarning(InputComponent<?, ?> input, String warningMessage)
   {
      Assert.notNull(warningMessage, "Warning message should not be null");
      warnings.add(warningMessage);
   }

   /**
    * @return the errors
    */
   public List<String> getErrors()
   {
      return errors;
   }

   /**
    * @return the warnings
    */
   public List<String> getWarnings()
   {
      return warnings;
   }
}
