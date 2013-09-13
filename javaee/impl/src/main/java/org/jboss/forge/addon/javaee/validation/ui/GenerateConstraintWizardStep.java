package org.jboss.forge.addon.javaee.validation.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.ui.InputComponentFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;

public class GenerateConstraintWizardStep extends AbstractJavaEECommand implements UIWizardStep
{
   public class AddPatternConstraintUIBuilder
   {

      public void build()
      {
         // TODO Auto-generated method stub

      }

   }

   public class AddFutureConstraintUIBuilder
   {

      public void build()
      {
         // TODO Auto-generated method stub

      }

   }

   public class AddPastConstraintUIBuilder
   {

      public void build()
      {
         // TODO Auto-generated method stub

      }

   }

   public class AddDigitsConstraintUIBuilder
   {

      public void build()
      {
         // TODO Auto-generated method stub

      }

   }

   public class AddSizeConstraintUIBuilder
   {

      public void build()
      {
         // TODO Auto-generated method stub

      }

   }

   public class AddDecimalMaxConstraintUIBuilder
   {

      public void build()
      {
         // TODO Auto-generated method stub

      }

   }

   public class AddDecimalMinConstraintUIBuilder
   {

      public void build()
      {
         // TODO Auto-generated method stub

      }

   }

   public class AddMaxConstraintUIBuilder
   {

      public void build()
      {
         // TODO Auto-generated method stub

      }

   }

   public class AddMinConstraintUIBuilder
   {

      public void build()
      {
         // TODO Auto-generated method stub

      }

   }

   public class AddAssertFalseConstraintUIBuilder
   {

      public void build()
      {
         // TODO Auto-generated method stub

      }

   }

   public class AddAssertTrueConstraintUIBuilder
   {

      public void build()
      {
         // TODO Auto-generated method stub

      }

   }

   public class AddNotNullConstraintUIBuilder
   {

      public void build()
      {
         // TODO Auto-generated method stub

      }

   }

   public class AddNullConstraintUIBuilder
   {

      public void build()
      {
         // TODO Auto-generated method stub

      }

   }

   @Inject
   private InputComponentFactory componentFactory;

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      return null;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      UIContext context = builder.getUIContext();
      Field<JavaClass> field = (Field<JavaClass>) context.getAttribute(Field.class);
      ConstraintType constraintType = (ConstraintType) context.getAttribute(ConstraintType.class);
      generateConstraintInputs(constraintType);
   }

   private void generateConstraintInputs(ConstraintType constraintType)
   {
      switch (constraintType)
      {
      case NULL:
         new AddNullConstraintUIBuilder().build();
         break;
      case NOT_NULL:
         new AddNotNullConstraintUIBuilder().build();
         break;
      case ASSERT_TRUE:
         new AddAssertTrueConstraintUIBuilder().build();
         break;
      case ASSERT_FALSE:
         new AddAssertFalseConstraintUIBuilder().build();
         break;
      case MIN:
         new AddMinConstraintUIBuilder().build();
         break;
      case MAX:
         new AddMaxConstraintUIBuilder().build();
         break;
      case DECIMAL_MIN:
         new AddDecimalMinConstraintUIBuilder().build();
         break;
      case DECIMAL_MAX:
         new AddDecimalMaxConstraintUIBuilder().build();
         break;
      case SIZE:
         new AddSizeConstraintUIBuilder().build();
         break;
      case DIGITS:
         new AddDigitsConstraintUIBuilder().build();
         break;
      case PAST:
         new AddPastConstraintUIBuilder().build();
         break;
      case FUTURE:
         new AddFutureConstraintUIBuilder().build();
         break;
      case PATTERN:
         new AddPatternConstraintUIBuilder().build();
         break;
      default:
         break;
      }
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      ConstraintType constraintType = (ConstraintType) context.getAttribute(ConstraintType.class);
      switch (constraintType)
      {
      case NULL:
         new AddNullConstraintUIBuilder().build();
         break;
      case NOT_NULL:
         new AddNotNullConstraintUIBuilder().build();
         break;
      case ASSERT_TRUE:
         new AddAssertTrueConstraintUIBuilder().build();
         break;
      case ASSERT_FALSE:
         new AddAssertFalseConstraintUIBuilder().build();
         break;
      case MIN:
         new AddMinConstraintUIBuilder().build();
         break;
      case MAX:
         new AddMaxConstraintUIBuilder().build();
         break;
      case DECIMAL_MIN:
         new AddDecimalMinConstraintUIBuilder().build();
         break;
      case DECIMAL_MAX:
         new AddDecimalMaxConstraintUIBuilder().build();
         break;
      case SIZE:
         new AddSizeConstraintUIBuilder().build();
         break;
      case DIGITS:
         new AddDigitsConstraintUIBuilder().build();
         break;
      case PAST:
         new AddPastConstraintUIBuilder().build();
         break;
      case FUTURE:
         new AddFutureConstraintUIBuilder().build();
         break;
      case PATTERN:
         new AddPatternConstraintUIBuilder().build();
         break;
      default:
         break;
      }
      return null;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }

}
