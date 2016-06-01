/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

import javax.persistence.MappedSuperclass;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class JPANewMappedSuperclassCommand extends AbstractJPACommand<JavaClassSource>
{

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("JPA: New Mapped Superclass")
               .description("Create a new JPA Mapped Superclass");
   }

   @Override
   protected String getType()
   {
      return "Mapped Superclass";
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
      source.addAnnotation(MappedSuperclass.class);
      return source;
   }
}
