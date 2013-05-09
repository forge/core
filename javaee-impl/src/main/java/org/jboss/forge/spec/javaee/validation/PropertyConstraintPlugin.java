/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.validation;

import static org.jboss.forge.shell.PromptType.JAVA_CLASS;
import static org.jboss.forge.shell.util.ResourceUtil.getJavaClassFromResource;

import java.io.FileNotFoundException;

import javax.inject.Inject;
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

import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.completer.PropertyCompleter;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.spec.javaee.ValidationFacet;
import org.jboss.forge.spec.javaee.validation.util.JavaHelper;

/**
 * @author Kevin Pollet
 */
@Alias("constraint")
@RequiresResource(JavaResource.class)
@RequiresFacet({ ValidationFacet.class, JavaSourceFacet.class })
public class PropertyConstraintPlugin implements Plugin
{
   private final JavaSourceFacet javaSourceFacet;
   private final Shell shell;

   @Inject
   public PropertyConstraintPlugin(Project project, Shell shell)
   {
      this.javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      this.shell = shell;
   }

   @Command(value = "Valid", help = "Adds @Valid constraint on the specified property")
   public void addValidConstraint(
            @Option(name = "onProperty", completer = PropertyCompleter.class, required = true) String property,
            @Option(name = "onAccessor", flagOnly = true) boolean onAccessor) throws FileNotFoundException
   {

      final Annotation<JavaClass> constraint = addConstraintOnProperty(property, onAccessor, Valid.class);

      javaSourceFacet.saveJavaSource(constraint.getOrigin());
      outputConstraintAdded(property, Valid.class);
   }

   @Command(value = "Null", help = "Adds @Null constraint on the specified property")
   public void addNullConstraint(
            @Option(name = "onProperty", completer = PropertyCompleter.class, required = true) String property,
            @Option(name = "onAccessor", flagOnly = true) boolean onAccessor,
            @Option(name = "message") String message,
            @Option(name = "groups", type = JAVA_CLASS) String[] groups) throws FileNotFoundException
   {

      final Annotation<JavaClass> constraint = addConstraintOnProperty(property, onAccessor, Null.class);
      setConstraintMessage(constraint, message);

      javaSourceFacet.saveJavaSource(constraint.getOrigin());
      outputConstraintAdded(property, Null.class);
   }

   @Command(value = "NotNull", help = "Adds @NotNull constraint on the specified property")
   public void addNotNullConstraint(
            @Option(name = "onProperty", completer = PropertyCompleter.class, required = true) String property,
            @Option(name = "onAccessor", flagOnly = true) boolean onAccessor,
            @Option(name = "message") String message,
            @Option(name = "groups", type = JAVA_CLASS) String[] groups) throws FileNotFoundException
   {

      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, NotNull.class);
      setConstraintMessage(constraintAnnotation, message);

      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      outputConstraintAdded(property, NotNull.class);
   }

   @Command(value = "AssertTrue", help = "Adds @AssertTrue constraint on the specified property")
   public void addAssertTrueConstraint(
            @Option(name = "onProperty", completer = PropertyCompleter.class, required = true) String property,
            @Option(name = "onAccessor", flagOnly = true) boolean onAccessor,
            @Option(name = "message") String message,
            @Option(name = "groups", type = JAVA_CLASS) String[] groups) throws FileNotFoundException
   {

      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, AssertTrue.class);
      setConstraintMessage(constraintAnnotation, message);

      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      outputConstraintAdded(property, AssertTrue.class);
   }

   @Command(value = "AssertFalse", help = "Adds @AssertFalse constraint on the specified property")
   public void addAssertFalseConstraint(
            @Option(name = "onProperty", completer = PropertyCompleter.class, required = true) String property,
            @Option(name = "onAccessor", flagOnly = true) boolean onAccessor,
            @Option(name = "message") String message,
            @Option(name = "groups", type = JAVA_CLASS) String[] groups) throws FileNotFoundException
   {

      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor,
               AssertFalse.class);
      setConstraintMessage(constraintAnnotation, message);

      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      outputConstraintAdded(property, AssertFalse.class);
   }

   @Command(value = "Min", help = "Adds @Min constraint on the specified property")
   public void addMinConstraint(
            @Option(name = "onProperty", completer = PropertyCompleter.class, required = true) String property,
            @Option(name = "onAccessor", flagOnly = true) boolean onAccessor,
            @Option(name = "min", required = true) long min,
            @Option(name = "message") String message,
            @Option(name = "groups", type = JAVA_CLASS) String[] groups) throws FileNotFoundException
   {

      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, Min.class);
      setConstraintMessage(constraintAnnotation, message);
      constraintAnnotation.setLiteralValue(String.valueOf(min));

      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      outputConstraintAdded(property, Min.class);
   }

   @Command(value = "Max", help = "Adds @Max constraint on the specified property")
   public void addMaxConstraint(
            @Option(name = "onProperty", completer = PropertyCompleter.class, required = true) String property,
            @Option(name = "onAccessor", flagOnly = true) boolean onAccessor,
            @Option(name = "max", required = true) long max,
            @Option(name = "message") String message,
            @Option(name = "groups", type = JAVA_CLASS) String[] groups) throws FileNotFoundException
   {

      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, Max.class);
      setConstraintMessage(constraintAnnotation, message);
      constraintAnnotation.setLiteralValue(String.valueOf(max));

      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      outputConstraintAdded(property, Max.class);
   }

   @Command(value = "DecimalMin", help = "Adds @DecimalMin constraint on the specified property")
   public void addDecimalMinConstraint(
            @Option(name = "onProperty", completer = PropertyCompleter.class, required = true) String property,
            @Option(name = "onAccessor", flagOnly = true) boolean onAccessor,
            @Option(name = "min", required = true) String min,
            @Option(name = "message") String message,
            @Option(name = "groups", type = JAVA_CLASS) String[] groups) throws FileNotFoundException
   {

      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, DecimalMin.class);
      setConstraintMessage(constraintAnnotation, message);
      constraintAnnotation.setStringValue(min);

      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      outputConstraintAdded(property, DecimalMin.class);
   }

   @Command(value = "DecimalMax", help = "Adds @DecimalMax constraint on the specified property")
   public void addDecimalMaxConstraint(
            @Option(name = "onProperty", completer = PropertyCompleter.class, required = true) String property,
            @Option(name = "onAccessor", flagOnly = true) boolean onAccessor,
            @Option(name = "max", required = true) String max,
            @Option(name = "message") String message,
            @Option(name = "groups", type = JAVA_CLASS) String[] groups) throws FileNotFoundException
   {

      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, DecimalMax.class);
      setConstraintMessage(constraintAnnotation, message);
      constraintAnnotation.setStringValue(max);

      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      outputConstraintAdded(property, DecimalMax.class);
   }

   @Command(value = "Size", help = "Adds @Size constraint on the specified property")
   public void addSizeConstraint(
            @Option(name = "onProperty", completer = PropertyCompleter.class, required = true) String property,
            @Option(name = "onAccessor", flagOnly = true) boolean onAccessor,
            @Option(name = "min") Integer min,
            @Option(name = "max") Integer max,
            @Option(name = "message") String message,
            @Option(name = "groups", type = JAVA_CLASS) String[] groups) throws FileNotFoundException
   {

      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, Size.class);
      setConstraintMessage(constraintAnnotation, message);

      if (min != null)
      {
         constraintAnnotation.setLiteralValue("min", String.valueOf(min));
      }

      if (max != null)
      {
         constraintAnnotation.setLiteralValue("max", String.valueOf(max));
      }

      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      outputConstraintAdded(property, Size.class);
   }

   @Command(value = "Digits", help = "Adds @Digits constraint on the specified property")
   public void addDigitsConstraint(
            @Option(name = "onProperty", completer = PropertyCompleter.class, required = true) String property,
            @Option(name = "onAccessor", flagOnly = true) boolean onAccessor,
            @Option(name = "integer", required = true) int integer,
            @Option(name = "fraction", required = true) int fraction,
            @Option(name = "message") String message,
            @Option(name = "groups", type = JAVA_CLASS) String[] groups) throws FileNotFoundException
   {

      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, Digits.class);
      setConstraintMessage(constraintAnnotation, message);
      constraintAnnotation.setLiteralValue("integer", String.valueOf(integer));
      constraintAnnotation.setLiteralValue("fraction", String.valueOf(fraction));

      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      outputConstraintAdded(property, Digits.class);
   }

   @Command(value = "Past", help = "Adds @Past constraint on the specified property")
   public void addPastConstraint(
            @Option(name = "onProperty", completer = PropertyCompleter.class, required = true) String property,
            @Option(name = "onAccessor", flagOnly = true) boolean onAccessor,
            @Option(name = "message") String message,
            @Option(name = "groups", type = JAVA_CLASS) String[] groups) throws FileNotFoundException
   {

      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, Past.class);
      setConstraintMessage(constraintAnnotation, message);

      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      outputConstraintAdded(property, Past.class);
   }

   @Command(value = "Future", help = "Adds @Future constraint on the specified property")
   public void addFutureConstraint(
            @Option(name = "onProperty", completer = PropertyCompleter.class, required = true) String property,
            @Option(name = "onAccessor", flagOnly = true) boolean onAccessor,
            @Option(name = "message") String message,
            @Option(name = "groups", type = JAVA_CLASS) String[] groups) throws FileNotFoundException
   {

      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, Future.class);
      setConstraintMessage(constraintAnnotation, message);

      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      outputConstraintAdded(property, Future.class);
   }

   @Command(value = "Pattern", help = "Adds @Pattern constraint on the specified property")
   public void addPatternConstraint(
            @Option(name = "onProperty", completer = PropertyCompleter.class, required = true) String property,
            @Option(name = "onAccessor", flagOnly = true) boolean onAccessor,
            @Option(name = "regexp", required = true) String regexp,
            @Option(name = "flags") Pattern.Flag[] flags,
            @Option(name = "message") String message,
            @Option(name = "groups", type = JAVA_CLASS) String[] groups) throws FileNotFoundException
   {

      final Annotation<JavaClass> constraintAnnotation = addConstraintOnProperty(property, onAccessor, Pattern.class);
      setConstraintMessage(constraintAnnotation, message);
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

      javaSourceFacet.saveJavaSource(constraintAnnotation.getOrigin());
      outputConstraintAdded(property, Pattern.class);
   }

   private Annotation<JavaClass> addConstraintOnProperty(String property, boolean onAccessor,
            Class<? extends java.lang.annotation.Annotation> annotationClass)
            throws FileNotFoundException
   {
      final Resource<?> currentResource = shell.getCurrentResource();
      final JavaClass clazz = getJavaClassFromResource(currentResource);
      final Field<JavaClass> field = clazz.getField(property);

      if (field == null)
      {
         throw new IllegalStateException("The current class has no property named '" + property + "'");
      }

      Member<JavaClass, ?> member = field;
      if (onAccessor)
      {
         final Method<JavaClass> accessor = JavaHelper.getFieldAccessor(field);
         if (accessor == null)
         {
            throw new IllegalStateException("The property named '" + property + "' has no accessor");
         }
         member = accessor;
      }

      if (member.hasAnnotation(annotationClass))
      {
         throw new IllegalStateException("The element '" + member.getName() + "' is already annotated with @"
                  + annotationClass.getSimpleName());
      }

      return member.addAnnotation(annotationClass);
   }

   private void setConstraintMessage(Annotation<JavaClass> annotation, String message)
   {
      if (message != null)
      {
         annotation.setStringValue("message", message);
      }
   }

   private void outputConstraintAdded(String property, Class<? extends java.lang.annotation.Annotation> constraintClass)
   {
      shell.println("Constraint " + constraintClass.getSimpleName()
               + " has been successfully added on property named '" + property + "'\n");
   }
}
