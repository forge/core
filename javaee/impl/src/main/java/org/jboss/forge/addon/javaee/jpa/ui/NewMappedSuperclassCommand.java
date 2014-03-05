/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui;

import javax.persistence.MappedSuperclass;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.ui.AbstractJavaSourceCommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class NewMappedSuperclassCommand extends AbstractJavaSourceCommand
{

   @Override
   protected String getType()
   {
      return "Mapped Superclass";
   }

   @Override
   protected Class<? extends JavaSource<?>> getSourceType()
   {
      return JavaClass.class;
   }

   @Override
   protected String calculateDefaultPackage(UIContext context)
   {
      String packageName;
      Project project = getSelectedProject(context);
      if (project != null)
      {
         packageName = project.getFacet(MetadataFacet.class).getTopLevelPackage() + ".model";
      }
      else
      {
         packageName = super.calculateDefaultPackage(context);
      }
      return packageName;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("JPA: New " + getType())
               .description("Creates a new " + getType())
               .category(Categories.create("JPA"));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Result result = super.execute(context);
      UIContext uiContext = context.getUIContext();
      Project project = getSelectedProject(uiContext);
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = context.getUIContext().getSelection();
      JavaSource<?> javaSource = javaResource.getJavaSource();
      javaSource.addAnnotation(MappedSuperclass.class);
      javaResource = javaSourceFacet.saveJavaSource(javaSource);
      uiContext.setSelection(javaResource);
      return result;
   }
}
