package org.jboss.forge.addon.javaee.validation;

import java.io.FileNotFoundException;

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

import org.jboss.forge.addon.parser.java.beans.Property;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;

public class ConstraintOperations
{

   public Result addValidConstraint(Project project, Property property, boolean onAccessor)
            throws FileNotFoundException
   {
      final Annotation<JavaClass> constraint = addConstraintOnProperty(property, onAccessor, Valid.class, null);

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(constraint.getOrigin());
      return outputConstraintAdded(property, Valid.class);
   }

   public Result addNullConstraint(Project project, Property property, boolean onAccessor, String message)
            throws FileNotFoundException
   {
      final Annotation<JavaClass> constraint = addConstraintOnProperty(property, onAccessor, Null.class, message);

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(constraint.getOrigin());
      return outputConstraintAdded(property, Null.class);
   }

   public Result addNotNullConstraint(Project project, Property property, boolean onAccessor, String message)
            throws FileNotFoundException
   {
      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, NotNull.class,
               message);

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      return outputConstraintAdded(property, NotNull.class);
   }

   public Result addAssertTrueConstraint(Project project, Property property, boolean onAccessor, String message)
            throws FileNotFoundException
   {
      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor,
               AssertTrue.class, message);

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      return outputConstraintAdded(property, AssertTrue.class);
   }

   public Result addAssertFalseConstraint(Project project, Property property, boolean onAccessor, String message)
            throws FileNotFoundException
   {
      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor,
               AssertFalse.class, message);

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      return outputConstraintAdded(property, AssertFalse.class);
   }

   public Result addMinConstraint(Project project, Property property, boolean onAccessor, String message, long min)
            throws FileNotFoundException
   {
      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, Min.class,
               message);
      constraintAnnotation.setLiteralValue(String.valueOf(min));

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      return outputConstraintAdded(property, Min.class);
   }

   public Result addMaxConstraint(Project project, Property property, boolean onAccessor, String message, long max)
            throws FileNotFoundException
   {
      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, Max.class,
               message);
      constraintAnnotation.setLiteralValue(String.valueOf(max));

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      return outputConstraintAdded(property, Max.class);
   }

   public Result addDecimalMinConstraint(Project project, Property property, boolean onAccessor, String message,
            String min) throws FileNotFoundException
   {
      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor,
               DecimalMin.class, message);
      constraintAnnotation.setStringValue(min);

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      return outputConstraintAdded(property, DecimalMin.class);
   }

   public Result addDecimalMaxConstraint(Project project, Property property, boolean onAccessor, String message,
            String max) throws FileNotFoundException
   {
      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor,
               DecimalMax.class, message);
      constraintAnnotation.setStringValue(max);

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      return outputConstraintAdded(property, DecimalMax.class);
   }

   public Result addSizeConstraint(Project project, Property property, boolean onAccessor, String message,
            Integer min, Integer max) throws FileNotFoundException
   {
      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, Size.class,
               message);

      if (min != null)
      {
         constraintAnnotation.setLiteralValue("min", String.valueOf(min));
      }

      if (max != null)
      {
         constraintAnnotation.setLiteralValue("max", String.valueOf(max));
      }

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      return outputConstraintAdded(property, Size.class);
   }

   public Result addDigitsConstraint(Project project, Property property, boolean onAccessor, String message,
            int integer, int fraction) throws FileNotFoundException
   {
      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, Digits.class,
               message);
      constraintAnnotation.setLiteralValue("integer", String.valueOf(integer));
      constraintAnnotation.setLiteralValue("fraction", String.valueOf(fraction));

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      return outputConstraintAdded(property, Digits.class);
   }

   public Result addPastConstraint(Project project, Property property, boolean onAccessor, String message)
            throws FileNotFoundException
   {
      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, Past.class,
               message);

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      return outputConstraintAdded(property, Past.class);
   }

   public Result addFutureConstraint(Project project, Property property, boolean onAccessor, String message
            ) throws FileNotFoundException
   {
      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, Future.class,
               message);

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      return outputConstraintAdded(property, Future.class);
   }

   public Result addPatternConstraint(Project project, Property property, boolean onAccessor, String message,
            int integer, int fraction, String regexp, Pattern.Flag[] flags) throws FileNotFoundException
   {
      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, Pattern.class,
               message);
      constraintAnnotation.setStringValue("regexp", regexp);

      if (flags != null)
      {
         constraintAnnotation.getOrigin().addImport(Pattern.Flag.class);
         final String flagPrefix = Pattern.Flag.class.getSimpleName() + ".";
         final StringBuilder flagsLiteral = new StringBuilder().append('{');

         for (int i = 0; i < flags.length; i++)
         {
            flagsLiteral.append(flagPrefix + flags[i]);

            if (i < (flags.length - 1))
            {
               flagsLiteral.append(",");
            }
         }

         flagsLiteral.append('}');
         constraintAnnotation.setStringValue("flags", flagsLiteral.toString());
      }

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      return outputConstraintAdded(property, Pattern.class);
   }

   private Annotation<JavaClass> addConstraintOnProperty(Property property, boolean onAccessor,
            Class<? extends java.lang.annotation.Annotation> annotationClass, String message)
            throws FileNotFoundException
   {
      Member<JavaClass, ?> member = property.getActualField();
      if (onAccessor)
      {
         final Method<JavaClass> accessor = property.getAccessor();
         if (accessor == null)
         {
            throw new IllegalStateException("The property named '" + property.getName() + "' has no accessor");
         }
         member = accessor;
      }

      if (member.hasAnnotation(annotationClass))
      {
         throw new IllegalStateException("The element '" + member.getName() + "' is already annotated with @"
                  + annotationClass.getSimpleName());
      }

      Annotation<JavaClass> annotation = member.addAnnotation(annotationClass);
      if (message != null)
      {
         annotation.setStringValue("message", message);
      }
      return annotation;
   }

   private Result outputConstraintAdded(Property property,
            Class<? extends java.lang.annotation.Annotation> constraintClass)
   {
      return Results.success("Constraint " + constraintClass.getSimpleName()
               + " has been successfully added on property named '" + property.getName());
   }

}
