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
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

import org.jboss.forge.addon.parser.java.resources.JavaResource;
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
   @Override
   public JavaClassSource newBackingBean(JavaClassSource source)
   {
      // Class
      source.addAnnotation(Named.class);
      return source;
   }

   @Override
   public JavaClassSource newConverter(JavaClassSource source)
   {
      // Class
      source.addInterface(Converter.class).addAnnotation(FacesConverter.class);

      // Methods
      MethodSource<?> getAsObject = source.addMethod().setPublic().setName("getAsObject").setReturnType(Object.class);
      getAsObject.addParameter(FacesContext.class, "context").setFinal(true);
      getAsObject.addParameter(UIComponent.class, "component").setFinal(true);
      getAsObject.addParameter(String.class, "value").setFinal(true);
      getAsObject.setBody("throw new UnsupportedOperationException(\"not yet implemented\");")
               .addAnnotation(Override.class);

      MethodSource<?> getAsString = source.addMethod().setPublic().setName("getAsString").setReturnType(String.class);
      getAsString.addParameter(FacesContext.class, "context").setFinal(true);
      getAsString.addParameter(UIComponent.class, "component").setFinal(true);
      getAsString.addParameter(Object.class, "value").setFinal(true);
      getAsString.setBody("return value.toString();").addAnnotation(Override.class);

      return source;
   }

   @Override
   public JavaClassSource newValidator(JavaClassSource source, String validatorName, String validatorPackage)
   {
      // Class
      source.addInterface(Validator.class);
      source.addImport(FacesMessage.class);
      source.addAnnotation(FacesValidator.class).setStringValue(validatorPackage + "." + validatorName);

      // Methods
      MethodSource<?> validateMethod = source.addMethod().setPublic().setName("validate").setReturnTypeVoid();
      validateMethod.addThrows(ValidatorException.class);
      validateMethod.addParameter(FacesContext.class, "context").setFinal(true);
      validateMethod.addParameter(UIComponent.class, "component").setFinal(true);
      validateMethod.addParameter(Object.class, "value").setFinal(true);
      validateMethod.setBody("throw new ValidatorException(new FacesMessage(\"Validator not yet implemented.\"));")
               .addAnnotation(Override.class);

      return source;
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