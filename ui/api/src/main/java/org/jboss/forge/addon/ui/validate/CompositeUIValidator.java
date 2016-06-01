/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.validate;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.furnace.util.Assert;

/**
 * A Composite implementation of a {@link UIValidator}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class CompositeUIValidator implements UIValidator
{

   private final List<UIValidator> validators = new ArrayList<UIValidator>();

   public CompositeUIValidator(Iterable<? extends UIValidator> validators)
   {
      Assert.notNull(validators, "Validators should not be null");
      for (UIValidator validator : validators)
      {
         Assert.notNull(validator, "Validator should not be null");
         this.validators.add(validator);
      }
   }

   public CompositeUIValidator(UIValidator firstValidator, UIValidator... validators)
   {
      Assert.notNull(firstValidator, "Validator should not be null");
      this.validators.add(firstValidator);
      Assert.notNull(validators, "Validators should not be null");
      for (UIValidator validator : validators)
      {
         Assert.notNull(validator, "Validator should not be null");
         this.validators.add(validator);
      }
   }

   @Override
   public void validate(UIValidationContext context)
   {
      for (UIValidator validator : validators)
      {
         validator.validate(context);
      }
   }

}
