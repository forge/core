/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.addon.validation;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.validation.Validation;
import javax.validation.Validator;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

/**
 * Produces a {@link Validator} object with support for {@link UnwrapValidatedValue}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ValidatorProducer
{
   @Produces
   @Singleton
   public Validator createValidator()
   {
      Validator validator = Validation.byProvider(HibernateValidator.class)
               .configure()
               .addValidatedValueHandler(new InputComponentValueUnwrapper())
               .buildValidatorFactory()
               .getValidator();
      return validator;
   }
}
