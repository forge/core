/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces.ui;

import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_FACES_CONVERTER_PACKAGE;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.faces.FacesOperations;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FacesNewConverterCommand extends AbstractFacesCommand<JavaClassSource>
{
   @Inject
   private FacesOperations facesOperations;

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
      return facesOperations.newConverter(source);
   }
}
