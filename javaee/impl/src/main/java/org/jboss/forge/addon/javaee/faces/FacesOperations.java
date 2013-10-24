/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.faces;

import java.io.FileNotFoundException;

import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.jboss.forge.addon.parser.java.JavaSourceFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;

/**
 * This class contains Faces specific operations
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class FacesOperations
{
   @Inject
   private JavaSourceFactory javaSourceFactory;

   /**
    * Creates a new {@link JavaResource} in the specified project. If no project is available, use
    * {@link FacesOperations#newConverter(DirectoryResource, String, String)}
    * 
    * @param project the current project to create the converter. Must not be null
    * @param converterName the name of the converter
    * @param converterPackage the package of the converter to be created
    * @return the created {@link JavaResource}
    */
   public JavaResource newConverter(Project project, String converterName, String converterPackage)
            throws FileNotFoundException
   {
      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass javaClass = createConverter(converterName, converterPackage);
      return java.saveJavaSource(javaClass);
   }

   /**
    * Creates a new {@link JavaResource} in the specified target. If a project is available, use
    * {@link FacesOperations#newConverter(Project, String, String)}
    * 
    * @param target the target directory resource to create this class
    * @param converterName the name of the converter
    * @param converterPackage the package of the converter to be created
    * @return the created {@link JavaResource}
    */
   public JavaResource newConverter(DirectoryResource target, String converterName, String converterPackage)
   {
      JavaClass javaClass = createConverter(converterName, converterPackage);
      JavaResource javaResource = getJavaResource(target, javaClass.getName());
      javaResource.setContents(javaClass);
      return javaResource;
   }

   private JavaClass createConverter(String converterName, String converterPackage)
   {
      JavaClass source = javaSourceFactory.parse(JavaClass.class, getClass().getResourceAsStream("Converter.jv"));
      source.setName(converterName);
      source.setPackage(converterPackage);
      return source;
   }

   /**
    * Creates a new {@link JavaResource} in the specified project. If no project is available, use
    * {@link FacesOperations#newValidator(DirectoryResource, String, String)}
    * 
    * @param project the current project to create the validator. Must not be null
    * @param validatorName the name of the validator
    * @param validatorPackage the package of the validator to be created
    * @return the created {@link JavaResource}
    */
   public JavaResource newValidator(Project project, String validatorName, String validatorPackage)
            throws FileNotFoundException
   {
      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass javaClass = createValidator(validatorName, validatorPackage);
      return java.saveJavaSource(javaClass);
   }

   /**
    * Creates a new {@link JavaResource} in the specified target. If a project is available, use
    * {@link FacesOperations#newConverter(Project, String, String)}
    * 
    * @param target the target directory resource to create the validator. Must not be null
    * @param validatorName the name of the validator
    * @param validatorPackage the package of the validator to be created
    * @return the created {@link JavaResource}
    */
   public JavaResource newValidator(DirectoryResource target, String converterName, String converterPackage)
   {
      JavaClass javaClass = createValidator(converterName, converterPackage);
      JavaResource javaResource = getJavaResource(target, javaClass.getName());
      javaResource.setContents(javaClass);
      return javaResource;
   }

   private JavaClass createValidator(String converterName, String converterPackage)
   {
      JavaClass source = javaSourceFactory.parse(JavaClass.class, getClass().getResourceAsStream("Validator.jv"));
      source.setName(converterName);
      source.setPackage(converterPackage);
      return source;
   }

   private JavaResource getJavaResource(final DirectoryResource sourceDir, final String relativePath)
   {
      String path = relativePath.trim().endsWith(".java")
               ? relativePath.substring(0, relativePath.lastIndexOf(".java")) : relativePath;
      path = path.replace(".", "/") + ".java";
      JavaResource target = sourceDir.getChildOfType(JavaResource.class, path);
      return target;
   }

   public Method<JavaClass> addValidatorMethod(JavaResource target, String name) throws FileNotFoundException
   {
      JavaClass source = (JavaClass) target.getJavaSource();
      Method<JavaClass> method = source.addMethod().setName(name)
               .setParameters("final FacesContext context, final UIComponent component, final Object value")
               .setBody("throw new ValidatorException(new FacesMessage(\"Validator not yet implemented.\"));")
               .addThrows(ValidatorException.class);
      method.getOrigin().addImport(ValidatorException.class);
      method.getOrigin().addImport(FacesMessage.class);
      target.setContents(source);
      return method;
   }
}