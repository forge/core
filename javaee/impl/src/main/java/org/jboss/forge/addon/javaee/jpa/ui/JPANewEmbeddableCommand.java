/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.jpa.PersistenceOperations;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;

public class JPANewEmbeddableCommand extends AbstractJPACommand<JavaClassSource>
{
   @Inject
   private PersistenceOperations persistenceOperations;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("JPA: New Embeddable")
               .description("Create a new JPA Embeddable");
   }

   @Override
   protected String getType()
   {
      return "JPA Embeddable";
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
      return persistenceOperations.newEmbeddableEntity(source);
   }
}
