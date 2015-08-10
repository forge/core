/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * Creates a new JSF Backing Bean
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public class FacesNewBeanCommand extends AbstractFacesCommand<JavaClassSource>
{
   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("Faces: New Bean")
               .description("Create a new JSF Backing Bean");
   }

   @Override
   protected String getType()
   {
      return "JSF Bean";
   }

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      source.addAnnotation(Inject.class);
      return source;
   }
}
