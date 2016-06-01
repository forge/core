/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui.methods;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.parser.java.beans.ProjectOperations;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.Extendable;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodHolderSource;
import org.jboss.forge.roaster.model.source.MethodSource;

@FacetConstraint(JavaSourceFacet.class)
public class JavaNewMethodCommandImpl extends AbstractProjectCommand implements JavaNewMethodCommand
{
   @Inject
   @WithAttributes(label = "Target Class", description = "The class where the method will be created", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaResource> targetClass;

   @Inject
   @WithAttributes(label = "Named", description = "The name of the method created", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Return", description = "The return type of the method created", type = InputType.JAVA_CLASS_PICKER, required = true, defaultValue = "String")
   private UIInput<String> returnType;

   @Inject
   @WithAttributes(label = "Parameters", description = "Parameters of the method created", required = false)
   private UIInput<String> parameters;

   @Inject
   @WithAttributes(label = "Access Type", description = "The access type", type = InputType.RADIO)
   private UISelectOne<Visibility> accessType;

   @Inject
   private ProjectOperations projectOperations;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Java: New Method")
               .description("Generates methods for the given Java class")
               .category(Categories.create("Java"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      setupTargetClass(builder.getUIContext());
      setupAccessType();
      builder.add(targetClass).add(returnType).add(parameters).add(accessType).add(named);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaResource javaResource = targetClass.getValue();
      JavaSource<?> targetclass = javaResource.getJavaType();

      String name = named.getValue();
      String returntype = returnType.getValue();
      String parameterString = parameters.getValue();
      Visibility visibility = accessType.getValue();

      // Map to store the parameter names as keys and the corresponding parameter types as values.
      Map<String, String> parametersMap = new LinkedHashMap<>();

      if (!Strings.isNullOrEmpty(parameterString))
      {
         String[] parametersArray = Strings.split(parameterString, ",");

         for (String parameter : parametersArray)
         {
            parameter = parameter.trim();
            String x[] = parameter.split(" ");
            parametersMap.put(x[1], x[0]);
         }
      }

      Object[] paramtypes = parametersMap.values().toArray();
      String[] parameterTypes = Arrays.copyOf(paramtypes, paramtypes.length, String[].class);

      MethodSource<JavaClassSource> superClassMethod = null;
      if (targetclass instanceof Extendable)
      {
         superClassMethod = inspectSuperClasses(context,
                  ((Extendable<?>) targetclass).getSuperType(), name,
                  parameterTypes);
      }

      /*
       * Implementation of method overriding rules:
       * 
       * private, static and final methods can not be overridden. Overriding method can not reduce access of overridden
       * method. Return type of overriding method must be same as overridden method.
       */

      if (superClassMethod != null)
      {
         if (!superClassMethod.isPrivate())
         {

            if (superClassMethod.isStatic())
            {

               return Results.fail("Method was already present and was static in the super class");
            }

            if (superClassMethod.isFinal() && !superClassMethod.isPrivate())
            {

               return Results.fail("Method was already present and was final in the super class");
            }
            if (!superClassMethod.getReturnType().toString().equals(returntype))
            {

               return Results.fail("Method was already present and had a different return type in the super class");
            }

            if (superClassMethod.getVisibility().compareTo(visibility) < 0)
            {

               return Results.fail("Method was already present and had higher access in the super class");
            }
         }
      }

      // Checks for already existing same signature method declarations in the target class
      MethodHolderSource<?> methodHolder = (MethodHolderSource<?>) targetclass;
      if (methodHolder.getMethod(name, parameterTypes) != null)
      {
         return Results.fail("Method was already present in the target class");
      }

      MethodSource<?> newMethod = methodHolder.addMethod();

      newMethod.setName(name);
      newMethod.setReturnType(returntype);
      newMethod.setVisibility(visibility);

      // Adding parameters to the newly created function from the parametersMap
      if (!Strings.isNullOrEmpty(parameterString))
      {
         for (Map.Entry<String, String> entry : parametersMap.entrySet())
            newMethod.addParameter(entry.getValue(), entry.getKey());
      }

      if (!targetclass.isInterface())
      {
         newMethod.setBody("throw new UnsupportedOperationException(\"Not supported yet.\");");
      }

      // Add @Override annotation based on super class overriding rules.
      if (superClassMethod != null && !superClassMethod.isPrivate())
         newMethod.addAnnotation("Override");

      setCurrentWorkingResource(context, targetclass);
      return Results.success("Method was generated successfully");

   }

   /*
    * Recursively scans the super class hierarchy to find already existing methods with similar signatures in super
    * classes. If an existing method with similar signature is found in super classes returns
    * MethodSource<JavaClassSource> object corresponding to that method. Otherwise null is returned.
    */
   private MethodSource<JavaClassSource> inspectSuperClasses(UIExecutionContext context, final String type,
            String name,
            String[] paramTypes)
   {

      Project project = getSelectedProject(context);
      JavaSource<?> clazz = sourceForName(project, type);

      if (clazz instanceof JavaClass)
      {
         JavaClassSource source = Roaster.parse(JavaClassSource.class, clazz.toString());
         MethodSource<JavaClassSource> superClassMethod = source.getMethod(name, paramTypes);

         if (superClassMethod != null)
         {
            return superClassMethod;
         }

         if (!source.getSuperType().equals("java.lang.Object"))
         {
            return inspectSuperClasses(context, source.getSuperType(), name, paramTypes);
         }
      }

      return null;
   }

   private static JavaSource<?> sourceForName(final Project project, final String type)
   {
      try
      {
         JavaSourceFacet javaSourceFact = project.getFacet(JavaSourceFacet.class);
         return javaSourceFact.getJavaResource(type).getJavaType();
      }
      catch (FileNotFoundException e)
      {

         return null;
      }
      catch (ResourceException e)
      {
         return null;
      }
   }

   private void setupTargetClass(UIContext context)
   {
      UISelection<FileResource<?>> selection = context.getInitialSelection();
      Project project = getSelectedProject(context);
      final List<JavaResource> entities = new ArrayList<>();
      entities.addAll(projectOperations.getProjectClasses(project));
      entities.addAll(projectOperations.getProjectInterfaces(project));
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

   private void setCurrentWorkingResource(UIExecutionContext context, JavaSource<?> javaClass)
            throws FileNotFoundException
   {
      Project selectedProject = getSelectedProject(context);

      if (selectedProject != null)
      {
         JavaSourceFacet facet = selectedProject.getFacet(JavaSourceFacet.class);
         facet.saveJavaSource(javaClass);
      }
      context.getUIContext().setSelection(javaClass);
   }

   private void setupAccessType()
   {
      accessType.setItemLabelConverter(new Converter<Visibility, String>()
      {
         @Override
         public String convert(Visibility source)
         {
            if (source == null)
               return null;
            if (source == Visibility.PACKAGE_PRIVATE)
            {
               return "default";
            }
            return source.toString();
         }
      });
      accessType.setDefaultValue(Visibility.PUBLIC);
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Inject
   ProjectFactory projectFactory;

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

}
