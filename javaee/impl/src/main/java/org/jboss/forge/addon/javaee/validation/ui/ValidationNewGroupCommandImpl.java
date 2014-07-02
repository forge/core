/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation.ui;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.ui.AbstractJavaSourceCommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;

/**
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public class ValidationNewGroupCommandImpl extends AbstractJavaSourceCommand<JavaInterfaceSource> implements
         ValidationNewGroupCommand
{

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("Constraint: New Group")
               .description("Create a Bean Validation group")
               .category(Categories.create(super.getMetadata(context).getCategory(), "Bean Validation"));
   }

   @Override
   protected String getType()
   {
      return "Bean Validation Group";
   }

   @Override
   protected Class<JavaInterfaceSource> getSourceType()
   {
      return JavaInterfaceSource.class;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected String calculateDefaultPackage(UIContext context)
   {
      return getSelectedProject(context).getFacet(JavaSourceFacet.class).getBasePackage() + ".constraints";
   }
}