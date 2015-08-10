/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces.ui;

import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_FACES_CONVERTER_PACKAGE;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FacesNewConverterCommand extends AbstractFacesCommand<JavaClassSource>
{
   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("Faces: New Converter")
               .description("Create a new JSF Converter");
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
               + DEFAULT_FACES_CONVERTER_PACKAGE;
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      // Class
      source.addInterface(Converter.class).addAnnotation(FacesConverter.class);

      // Methods
      MethodSource<?> getAsObject = source.addMethod().setPublic().setName("getAsObject")
               .setReturnType(Object.class);
      getAsObject.addParameter(FacesContext.class, "context").setFinal(true);
      getAsObject.addParameter(UIComponent.class, "component").setFinal(true);
      getAsObject.addParameter(String.class, "value").setFinal(true);
      getAsObject.setBody("throw new UnsupportedOperationException(\"not yet implemented\");")
               .addAnnotation(Override.class);

      MethodSource<?> getAsString = source.addMethod().setPublic().setName("getAsString")
               .setReturnType(String.class);
      getAsString.addParameter(FacesContext.class, "context").setFinal(true);
      getAsString.addParameter(UIComponent.class, "component").setFinal(true);
      getAsString.addParameter(Object.class, "value").setFinal(true);
      getAsString.setBody("return value.toString();")
               .addAnnotation(Override.class);

      return source;
   }
}
