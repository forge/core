/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.faces;

import java.io.FileNotFoundException;

import javax.faces.validator.Validator;

import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

/**
 * This class contains Faces specific operations
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface FacesOperations
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
   public JavaResource newBackingBean(Project project, String backingBeanName, String backingBeanPackage)
            throws FileNotFoundException;

   /**
    * Creates a new {@link JavaResource} in the specified target. If a project is available, use
    * {@link FacesOperations#newBackingBean(Project, String, String)}
    * 
    * @param target the target directory resource to create the backing bean
    * @param backingBeanName the name of the backing bean
    * @param backingBeanPackage the package of the backing bean to be created
    * @return the created {@link JavaResource}
    */
   public JavaResource newBackingBean(DirectoryResource target, String backingBeanName, String backingBeanPackage);

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
            throws FileNotFoundException;

   /**
    * Creates a new {@link JavaResource} in the specified target. If a project is available, use
    * {@link FacesOperations#newConverter(Project, String, String)}
    * 
    * @param target the target directory resource to create this class
    * @param converterName the name of the converter
    * @param converterPackage the package of the converter to be created
    * @return the created {@link JavaResource}
    */
   public JavaResource newConverter(DirectoryResource target, String converterName, String converterPackage);

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
            throws FileNotFoundException;

   /**
    * Creates a new {@link JavaResource} in the specified target. If a project is available, use
    * {@link FacesOperations#newConverter(Project, String, String)}
    * 
    * @param target the target directory resource to create the validator. Must not be null
    * @param validatorName the name of the validator
    * @param validatorPackage the package of the validator to be created
    * @return the created {@link JavaResource}
    */
   public JavaResource newValidator(DirectoryResource target, String validatorName, String validatorPackage);

   /**
    * Adds a {@link Validator} method to the given {@link JavaResource}.
    */
   public MethodSource<JavaClassSource> addValidatorMethod(JavaResource target, String name)
            throws FileNotFoundException;
}