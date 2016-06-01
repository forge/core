/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui.annotations;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.parser.java.beans.ProjectOperations;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaFieldResource;
import org.jboss.forge.addon.parser.java.resources.JavaMethodResource;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.resource.util.ResourceUtil;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.Parameter;
import org.jboss.forge.roaster.model.ValuePair;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.AnnotationTargetSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * @author <a href="mailto:robert@balent.cz">Robert Balent</a>
 */
@FacetConstraint(JavaSourceFacet.class)
public class JavaAddAnnotationCommandImpl extends AbstractProjectCommand implements JavaAddAnnotationCommand
{
   @Inject
   @WithAttributes(label = "Target Class", description = "The class where the annotation will be added", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaResource> targetClass;

   @Inject
   @WithAttributes(label = "Annotation", description = "The annotation which will be added", required = true, type = InputType.DEFAULT)
   private UIInput<String> annotation;

   @Inject
   @WithAttributes(label = "Target Property", description = "The property where the annotation will be added", required = false, type = InputType.DROPDOWN)
   private UISelectOne<JavaFieldResource> onProperty;

   @Inject
   @WithAttributes(label = "Target Method", description = "The method where the annotation will be added", required = false, type = InputType.DROPDOWN)
   private UISelectOne<JavaMethodResource> onMethod;

   @Inject
   private ProjectOperations projectOperations;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Java: Add Annotation")
               .description("Add annotation to class, property or method.")
               .category(Categories.create("Java"));
   }

   @Override
   public void initializeUI(final UIBuilder builder)
   {
      setupTargetClass(builder.getUIContext());

      onProperty.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call()
         {
            if (onMethod.getValue() != null || onProperty.getValue() != null)
            {
               return false;
            }

            JavaResource javaResource = targetClass.getValue();

            if (javaResource != null)
            {
               return ResourceUtil.filterByType(JavaFieldResource.class, javaResource.listResources()).size() > 0;
            }
            return false;
         }
      });

      onMethod.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call()
         {
            if (onMethod.getValue() != null || onProperty.getValue() != null)
            {
               return false;
            }

            JavaResource javaResource = targetClass.getValue();

            if (javaResource != null)
            {
               return ResourceUtil.filterByType(JavaMethodResource.class, javaResource.listResources()).size() > 0;
            }
            return false;
         }
      });

      onProperty.setValueChoices(new Callable<Iterable<JavaFieldResource>>()
      {
         @Override
         public Iterable<JavaFieldResource> call()
         {
            JavaResource javaResource = targetClass.getValue();
            if (javaResource != null)
               return ResourceUtil.filterByType(JavaFieldResource.class, javaResource.listResources());
            return Collections.emptyList();
         }
      });

      onMethod.setValueChoices(new Callable<Iterable<JavaMethodResource>>()
      {
         @Override
         public Iterable<JavaMethodResource> call()
         {
            JavaResource javaResource = targetClass.getValue();
            if (javaResource != null)
               return ResourceUtil.filterByType(JavaMethodResource.class, javaResource.listResources());
            return Collections.emptyList();
         }
      });

      onProperty.setItemLabelConverter(new Converter<JavaFieldResource, String>()
      {
         @Override
         public String convert(JavaFieldResource source)
         {
            return (source == null ? null : source.getUnderlyingResourceObject().getName());
         }
      });

      onMethod.setItemLabelConverter(new Converter<JavaMethodResource, String>()
      {
         @Override
         public String convert(JavaMethodResource source)
         {
            return (source == null ? null : source.getUnderlyingResourceObject().getName());
         }
      });

      annotation.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input,
                  String value)
         {
            Project project = getSelectedProject(builder.getUIContext());
            List<JavaResource> javaClasses = projectOperations.getProjectAnnotations(project);
            List<String> projectAnnotations = new ArrayList<>();
            for (JavaResource javaResource : javaClasses)
            {
               try
               {
                  projectAnnotations.add(javaResource.getJavaType().getCanonicalName());
               }
               catch (FileNotFoundException | ResourceException ignored)
               {
                  // don't mind
               }
            }
            return projectAnnotations;
         }
      });

      builder.add(targetClass).add(annotation).add(onProperty).add(onMethod);
   }

   @SuppressWarnings("unchecked")
   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaClassSource javaSource = targetClass.getValue().getJavaType();

      Result result;

      if (onProperty.hasValue())
      {
         String propertyName = onProperty.getValue().getUnderlyingResourceObject().getName();
         AnnotationTargetSource<JavaClassSource, ?> field = javaSource.getField(propertyName);

         addAnnotationToSource(field, annotation.getValue());

         result = Results.success("Annotation \"" + annotation.getValue() + "\" was successfully added to \""
                  + propertyName + "\" property declaration.");
      }
      else if (onMethod.hasValue())
      {
         List<Parameter<?>> parameters = onMethod.getValue().getUnderlyingResourceObject().getParameters();

         String[] stringParametersArray = new String[parameters.size()];

         for (int i = 0; i < parameters.size(); i++)
         {
            stringParametersArray[i] = parameters.get(i).getType().getName();
         }

         String methodName = onMethod.getValue().getUnderlyingResourceObject().getName();
         AnnotationTargetSource<JavaClassSource, ?> method = javaSource.getMethod(methodName, stringParametersArray);

         addAnnotationToSource(method, annotation.getValue());

         result = Results.success("Annotation \"" + annotation.getValue() + "\" was successfully added to the \""
                  + methodName + "\" method declaration.");
      }
      else
      {
         addAnnotationToSource(javaSource, annotation.getValue());

         result = Results.success("Annotation \"" + annotation.getValue()
                  + "\" was successfully added to the class declaration.");
      }

      getSelectedProject(context).getFacet(JavaSourceFacet.class).saveJavaSource(javaSource);

      return result;
   }

   private void addAnnotationToSource(AnnotationTargetSource<JavaClassSource, ?> targetSource, String annotationStr)
   {
      String annotationClassName = getAnnotationClassNameFromString(annotationStr);

      AnnotationSource<JavaClassSource> annotationToRemove = targetSource.getAnnotation(annotationClassName);

      if (annotationToRemove != null)
      {
         targetSource.removeAnnotation(annotationToRemove);
      }

      AnnotationSource<JavaClassSource> annotationSource;

      try
      {
         annotationSource = targetSource.addAnnotation(annotationClassName);
      }
      catch (Exception ex)
      {
         throw new IllegalArgumentException("Annotation with name \"" + annotationClassName
                  + "\" couldn't be added. Are you sure it's correct?");
      }

      populateAnnotationFromString(annotationSource, annotation.getValue());
   }

   private void populateAnnotationFromString(AnnotationSource<JavaClassSource> annotationSource, String str)
   {
      String stub = "@" + str + " public class Stub { }";
      JavaClass<?> parsedClass;
      try
      {
         parsedClass = Roaster.parse(JavaClass.class, stub);
      }
      catch (Exception ex)
      {
         throw new IllegalArgumentException("Can't parse annotation \"" + str + "\". Are you sure it's correct?");
      }

      if (parsedClass.getAnnotations().size() == 0)
      {
         throw new IllegalArgumentException("Can't parse annotation \"" + str + "\". Are you sure it's correct?");
      }

      List<ValuePair> valuePairs = parsedClass.getAnnotations().get(0).getValues();

      for (ValuePair valuePair : valuePairs)
      {
         if ("$missing$".equals(valuePair.getLiteralValue()))
         {
            throw new IllegalArgumentException(
                     "Parameter \"" + valuePair.getName() + "\" is missing or is incomplete.");
         }
         annotationSource.setLiteralValue(valuePair.getName(), valuePair.getLiteralValue());
      }
   }

   private String getAnnotationClassNameFromString(String annotationString)
   {
      int leftParenthesisIndex = annotationString.indexOf('(');
      if (leftParenthesisIndex > -1)
      {
         return annotationString.substring(0, leftParenthesisIndex);
      }
      return annotationString;
   }

   private void setupTargetClass(UIContext context)
   {
      UISelection<FileResource<?>> selection = context.getInitialSelection();
      Project project = getSelectedProject(context);
      final List<JavaResource> entities = projectOperations.getProjectClasses(project);
      targetClass.setValueChoices(entities);
      int idx = -1;
      if (!selection.isEmpty())
      {
         idx = entities.indexOf(selection.get());
      }
      if (idx != -1)
      {
         targetClass.setDefaultValue(entities.get(idx));
      }
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Inject
   private ProjectFactory projectFactory;

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }
}