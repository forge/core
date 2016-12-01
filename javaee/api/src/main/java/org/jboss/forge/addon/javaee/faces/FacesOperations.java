/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces;

import java.io.FileNotFoundException;

import javax.faces.validator.Validator;

import org.jboss.forge.addon.javaee.cdi.ui.BeanScope;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
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
    * Creates a new JSF Backing Bean
    * 
    * @param source the current source to decorate
    * @return the decorated {@link JavaResource}
    */
   JavaClassSource newBackingBean(JavaClassSource source, BeanScope scope);

   /**
    * Creates a new JSF Converter
    *
    * @param source the current source to decorate
    * @return the decorated {@link JavaResource}
    */
   JavaClassSource newConverter(JavaClassSource source);

   /**
    * Creates a new JSF Validator
    *
    * @param source the current source to decorate
    * @return the decorated {@link JavaResource}
    */
   JavaClassSource newValidator(JavaClassSource source, String validatorName, String validatorPackage);

   /**
    * Adds a {@link Validator} method to the given {@link JavaResource}.
    */
   MethodSource<JavaClassSource> addValidatorMethod(JavaResource target, String name)
            throws FileNotFoundException;
}