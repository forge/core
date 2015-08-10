/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces.ui;

import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_FACES_CONVERTER_PACKAGE;
import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_FACES_VALIDATOR_PACKAGE;

import java.io.FileNotFoundException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.jboss.forge.addon.javaee.faces.FacesOperations;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FacesNewValidatorCommand extends AbstractFacesCommand<JavaClassSource>
{
   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("Faces: New Validator")
               .description("Create a new JSF Validator");
   }

   @Override
   protected String getType()
   {
      return "JSF Converter";
   }

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }

   @Override
   protected String calculateDefaultPackage(UIContext context)
   {
      return getSelectedProject(context).getFacet(JavaSourceFacet.class).getBasePackage() + "."
              + DEFAULT_FACES_VALIDATOR_PACKAGE;
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      // Class
      source.addInterface(Validator.class);
      source.addImport(FacesMessage.class);
      source.addAnnotation(FacesValidator.class).setStringValue(getTargetPackage().getValue() + "." + getNamed().getValue());

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
}
