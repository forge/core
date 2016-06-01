/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.cdi.CDIOperations;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.ClassLoaderFacet;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.roaster.model.source.AnnotationElementSource;
import org.jboss.forge.roaster.model.source.JavaAnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.util.Types;

/**
 * Creates a new CDI Annotation Literal class
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class CDINewAnnotationLiteralCommand extends AbstractCDICommand<JavaClassSource>
{
   @Inject
   @WithAttributes(label = "Qualifier", type = InputType.JAVA_CLASS_PICKER, required = true)
   private UIInput<String> qualifier;

   @Inject
   private CDIOperations cdiOperations;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("CDI: New Annotation Literal")
               .description("Creates an Annotation Literal Type");
   }

   @Override
   protected String getType()
   {
      return "CDI Annotation Literal Type";
   }

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      setupQualifiers();
      builder.add(qualifier);
   }

   private void setupQualifiers()
   {
      qualifier.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(final UIContext context, final InputComponent<?, String> input,
                  final String value)
         {
            final Project project = getSelectedProject(context);
            final List<String> options = new ArrayList<>();
            for (String type : CDIOperations.DEFAULT_QUALIFIERS)
            {
               if (Strings.isNullOrEmpty(value) || type.startsWith(value))
               {
                  options.add(type);
               }
            }
            if (project != null)
            {
               for (JavaResource resource : cdiOperations.getProjectQualifiers(project))
               {
                  try
                  {
                     JavaSource<?> javaSource = resource.getJavaType();
                     String qualifiedName = javaSource.getQualifiedName();
                     if (Strings.isNullOrEmpty(value) || qualifiedName.startsWith(value))
                     {
                        options.add(qualifiedName);
                     }
                  }
                  catch (FileNotFoundException ignored)
                  {
                  }
               }
            }
            return options;
         }
      });
   }

   @Override
   protected boolean supportsExtends()
   {
      return false;
   }

   @Override
   protected boolean supportsImplements()
   {
      return false;
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource annotationLiteral)
            throws Exception
   {
      String qualifierClassName = qualifier.getValue();
      Map<String, String> nameTypeMap = extractQualifierMethods(project);
      annotationLiteral.addImport(qualifierClassName);
      annotationLiteral.addImport(javax.enterprise.util.AnnotationLiteral.class);
      annotationLiteral.setSuperType("AnnotationLiteral<" + Types.toSimpleName(qualifierClassName) + ">").addInterface(
               qualifierClassName);

      StringBuilder constructorBody = new StringBuilder();

      MethodSource<JavaClassSource> constructor = annotationLiteral.addMethod().setConstructor(true).setPublic();
      for (Entry<String, String> entry : nameTypeMap.entrySet())
      {
         String name = entry.getKey();
         String type = entry.getValue();
         // Fields
         annotationLiteral.addField().setPrivate().setFinal(true).setName(name).setType(type);
         // Interface methods
         annotationLiteral.addMethod().setPublic().setName(name).setReturnType(type)
                  .setBody("return this." + name + ";");
         constructor.addParameter(type, name);
         constructorBody.append("this.").append(name).append("=").append(name).append(";")
                  .append(System.lineSeparator());

      }
      constructor.setBody(constructorBody.toString());
      return annotationLiteral;
   }

   private Map<String, String> extractQualifierMethods(Project project) throws ClassNotFoundException
   {
      Map<String, String> nameTypeMap = new LinkedHashMap<>();
      String qualifierClassName = qualifier.getValue();
      boolean done = false;
      try
      {
         JavaResource javaResource = project.getFacet(JavaSourceFacet.class).getJavaResource(qualifierClassName);
         if (javaResource.exists())
         {
            done = true;
            if (!javaResource.getJavaType().isAnnotation())
            {
               throw new RuntimeException("Specified qualifier is not an annotation: " + qualifierClassName);
            }
            JavaAnnotationSource ann = javaResource.getJavaType();
            for (AnnotationElementSource elem : ann.getAnnotationElements())
            {
               String name = elem.getName();
               // Workaround for Class<?> parameters
               String type = "Class".equals(elem.getType().getName()) ? "Class<?>" : elem.getType()
                        .getQualifiedName();
               nameTypeMap.put(name, type);
            }
         }
      }
      catch (ResourceException | FileNotFoundException e)
      {
         // Do nothing
      }
      if (!done)
      {
         // Fallback to ClassLoaderFacet
         try (URLClassLoader loader = project.getFacet(ClassLoaderFacet.class).getClassLoader())
         {
            Class<?> qualifierClass = loader.loadClass(qualifierClassName);
            if (!qualifierClass.isAnnotation())
            {
               throw new RuntimeException("Specified qualifier is not an annotation: " + qualifierClassName);
            }
            for (Method m : qualifierClass.getDeclaredMethods())
            {
               String name = m.getName();
               // Workaround for Class<?> parameters
               String type = m.getReturnType() == Class.class ? "Class<?>" : m.getReturnType().getName();
               nameTypeMap.put(name, type);
            }
         }
         catch (IOException e)
         {
            // Ignored
         }
      }
      return nameTypeMap;
   }
}
