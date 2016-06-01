/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.concurrency.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.concurrency.ConcurrencyFacet;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraint;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Setups Concurrency in your JavaEE project
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint({ DependencyFacet.class, ResourcesFacet.class })
@StackConstraint({ ConcurrencyFacet.class })
public class ConcurrencySetupCommandImpl extends AbstractJavaEECommand
{
   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("Concurrency: Setup")
               .description("Setup Concurrency in your Java EE project")
               .category(Categories.create(super.getMetadata(context).getCategory(), "Concurrency"));
   }

   @Inject
   private FacetFactory facetFactory;

   @Inject
   @WithAttributes(required = true, label = "Concurrency Version", defaultValue = "1.0")
   private UISelectOne<ConcurrencyFacet> concurrencyVersion;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Project project = getSelectedProject(builder);
      if (filterValueChoicesFromStack(project, concurrencyVersion))
      {
         builder.add(concurrencyVersion);
      }
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return super.isEnabled(context) && !getSelectedProject(context).hasFacet(ConcurrencyFacet.class);
   }

   @Override
   public Result execute(final UIExecutionContext context) throws Exception
   {
      ConcurrencyFacet value = concurrencyVersion.getValue();
      if (facetFactory.install(getSelectedProject(context.getUIContext()), value))
      {
         return Results.success(value.toString() + " has been installed.");
      }
      return Results.fail("Could not install Concurrency.");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }
}
