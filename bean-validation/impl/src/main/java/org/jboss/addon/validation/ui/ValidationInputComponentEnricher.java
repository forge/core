/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.addon.validation.ui;

import javax.inject.Inject;
import javax.validation.Validator;

import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.inject.InputComponentInjectionEnricher;
import org.jboss.forge.addon.ui.input.inject.InputComponentInjectionPoint;
import org.jboss.forge.addon.ui.validate.UIValidator;

/**
 * Enables Bean Validation 1.1 as a {@link UIValidator} object in the {@link InputComponent}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ValidationInputComponentEnricher implements InputComponentInjectionEnricher
{
   @Inject
   private Validator validator;

   @Override
   public void enrich(InputComponentInjectionPoint injectionPoint, InputComponent<?, ?> input)
   {
      Class<?> beanType = injectionPoint.getBeanClass();
      UIValidationAdapter adapter = new UIValidationAdapter(validator, input, beanType);
      input.addValidator(adapter);
   }
}
