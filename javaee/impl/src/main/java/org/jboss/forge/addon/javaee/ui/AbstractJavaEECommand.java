/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.ui;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

public abstract class AbstractJavaEECommand extends AbstractProjectCommand
{
   @Inject
   private ProjectFactory projectFactory;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).category(Categories.create("Java EE"));
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

   public List<Class<? extends UICommand>> getSetupSteps(UIContext context)
   {
      return Collections.emptyList();
   }
}
