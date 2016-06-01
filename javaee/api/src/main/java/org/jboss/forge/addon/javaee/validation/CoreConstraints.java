/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation;

import java.lang.annotation.Annotation;

import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public enum CoreConstraints implements ConstraintType
{
   VALID(Valid.class),
   NULL(Null.class),
   NOT_NULL(NotNull.class),
   ASSERT_TRUE(AssertTrue.class),
   ASSERT_FALSE(AssertFalse.class),
   MIN(Min.class),
   MAX(Max.class),
   DECIMAL_MIN(DecimalMin.class),
   DECIMAL_MAX(DecimalMax.class),
   SIZE(Size.class),
   DIGITS(Digits.class),
   PAST(Past.class),
   FUTURE(Future.class),
   PATTERN(Pattern.class);

   private Class<? extends Annotation> constraint;

   CoreConstraints(Class<? extends Annotation> constraint)
   {
      this.constraint = constraint;
   }

   @Override
   public Class<? extends Annotation> getConstraint()
   {
      return constraint;
   }

   @Override
   public String getDescription()
   {
      return constraint.getSimpleName();
   }
}
