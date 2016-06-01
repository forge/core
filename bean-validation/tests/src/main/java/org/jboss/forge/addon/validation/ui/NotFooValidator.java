/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.validation.ui;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class NotFooValidator implements ConstraintValidator<NotFoo, Object>
{

   @Override
   public void initialize(NotFoo constraintAnnotation)
   {
   }

   @Override
   public boolean isValid(Object value, ConstraintValidatorContext context)
   {
      return !"Foo".equals(value);
   }

}
