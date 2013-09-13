package org.jboss.forge.addon.javaee.validation.ui;

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

public enum ConstraintType
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

   private Class<?> constraint;

   ConstraintType(Class<?> constraint)
   {
      this.constraint = constraint;
   }

   public String getDescription()
   {
      return constraint.getSimpleName();
   }
}
