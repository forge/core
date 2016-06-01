/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.cdi.CDIFacet;
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
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint({ DependencyFacet.class, ResourcesFacet.class })
@StackConstraint({ CDIFacet.class })
public class CDISetupCommandImpl extends AbstractJavaEECommand implements CDISetupCommand
{
   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("CDI: Setup")
               .description("Setup CDI in your project")
               .category(Categories.create(super.getMetadata(context).getCategory(), "CDI"));
   }

   @Inject
   private FacetFactory facetFactory;

   @Inject
   @WithAttributes(required = true, label = "CDI Version", defaultValue = "1.0")
   private UISelectOne<CDIFacet<?>> cdiVersion;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Project project = getSelectedProject(builder);
      if (filterValueChoicesFromStack(project, cdiVersion))
      {
         builder.add(cdiVersion);
      }
   }

   @Override
   public Result execute(final UIExecutionContext context) throws Exception
   {
      if (facetFactory.install(getSelectedProject(context.getUIContext()), cdiVersion.getValue()))
      {
         return Results.success("CDI has been installed.");
      }
      return Results.fail("Could not install CDI.");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }
}
