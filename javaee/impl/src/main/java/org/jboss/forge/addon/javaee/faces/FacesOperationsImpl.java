/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.faces;

import java.io.FileNotFoundException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

/**
 * This class contains Faces specific operations
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class FacesOperationsImpl implements FacesOperations
{
   /**
    * Creates a new {@link JavaResource} in the specified project. If no project is available, use
    * {@link FacesOperations#newBackingBean(DirectoryResource, String, String)}
    * 
    * @param project the current project to create the backing bean. Must not be null
    * @param backingBeanName the name of the backing bean
    * @param backingBeanPackage the package of the backing bean to be created
    * @return the created {@link JavaResource}
    */
   @Override
   public JavaResource newBackingBean(Project project, String backingBeanName, String backingBeanPackage)
            throws FileNotFoundException
   {
      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClassSource javaClass = createBackingBean(backingBeanName, backingBeanPackage);
      return java.saveJavaSource(javaClass);
   }

   /**
    * Creates a new {@link JavaResource} in the specified target. If a project is available, use
    * {@link FacesOperations#newBackingBean(Project, String, String)}
    * 
    * @param target the target directory resource to create the backing bean
    * @param backingBeanName the name of the backing bean
    * @param backingBeanPackage the package of the backing bean to be created
    * @return the created {@link JavaResource}
    */
   @Override
   public JavaResource newBackingBean(DirectoryResource target, String backingBeanName, String backingBeanPackage)
   {
      JavaClassSource javaClass = createBackingBean(backingBeanName, backingBeanPackage);
      JavaResource javaResource = getJavaResource(target, javaClass.getName());
      javaResource.setContents(javaClass);
      return javaResource;
   }

   /**
    * Creates a new {@link JavaResource} in the specified project. If no project is available, use
    * {@link FacesOperations#newConverter(DirectoryResource, String, String)}
    * 
    * @param project the current project to create the converter. Must not be null
    * @param converterName the name of the converter
    * @param converterPackage the package of the converter to be created
    * @return the created {@link JavaResource}
    */
   @Override
   public JavaResource newConverter(Project project, String converterName, String converterPackage)
            throws FileNotFoundException
   {
      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClassSource javaClass = createConverter(converterName, converterPackage);
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
   @Override
   public JavaResource newConverter(DirectoryResource target, String converterName, String converterPackage)
   {
      JavaClassSource javaClass = createConverter(converterName, converterPackage);
      JavaResource javaResource = getJavaResource(target, javaClass.getName());
      javaResource.setContents(javaClass);
      return javaResource;
   }

   private JavaClassSource createConverter(String converterName, String converterPackage)
   {
      JavaClassSource source = Roaster.parse(JavaClassSource.class, getClass().getResourceAsStream("Converter.jv"));
      source.setName(converterName);
      source.setPackage(converterPackage);
      return source;
   }

   private JavaClassSource createBackingBean(String beanName, String beanPackage)
   {
      JavaClassSource source = Roaster.parse(JavaClassSource.class, getClass().getResourceAsStream("BackingBean.jv"));
      source.setName(beanName);
      source.setPackage(beanPackage);
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
   @Override
   public JavaResource newValidator(Project project, String validatorName, String validatorPackage)
            throws FileNotFoundException
   {
      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClassSource javaClass = createValidator(validatorName, validatorPackage);
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
   @Override
   public JavaResource newValidator(DirectoryResource target, String validatorName, String validatorPackage)
   {
      JavaClassSource javaClass = createValidator(validatorName, validatorPackage);
      JavaResource javaResource = getJavaResource(target, javaClass.getName());
      javaResource.setContents(javaClass);
      return javaResource;
   }

   private JavaClassSource createValidator(String converterName, String converterPackage)
   {
      JavaClassSource source = Roaster.parse(JavaClassSource.class, getClass().getResourceAsStream("Validator.jv"));
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

   @Override
   public MethodSource<JavaClassSource> addValidatorMethod(JavaResource target, String name)
            throws FileNotFoundException
   {
      JavaClassSource source = target.getJavaType();
      MethodSource<JavaClassSource> method = source.addMethod().setName(name)
               .setParameters("final FacesContext context, final UIComponent component, final Object value")
               .setBody("throw new ValidatorException(new FacesMessage(\"Validator not yet implemented.\"));")
               .addThrows(ValidatorException.class);
      method.getOrigin().addImport(ValidatorException.class);
      method.getOrigin().addImport(FacesMessage.class);
      method.getOrigin().addImport(FacesContext.class);
      method.getOrigin().addImport(UIComponent.class);
      target.setContents(source);
      return method;
   }
}