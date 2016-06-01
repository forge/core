/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.facets.JavaEESpecFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class JavaEESetupCommand extends AbstractJavaEECommand
{
   @Inject
   private FacetFactory facetFactory;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   @WithAttributes(required = true, label = "Java EE Version", defaultValue = "6")
   private UISelectOne<JavaEESpecFacet> javaEEVersion;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("JavaEE: Setup")
               .description("Setup Java EE in your project");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(javaEEVersion);
   }

   @Override
   public Result execute(final UIExecutionContext context) throws Exception
   {
      JavaEESpecFacet chosen = javaEEVersion.getValue();
      if (facetFactory.install(getSelectedProject(context.getUIContext()), chosen))
      {
         // This facet may activate other facets, so better invalidate the cache
         projectFactory.invalidateCaches();
         return Results.success("JavaEE " + chosen.getSpecVersion() + " has been installed.");
      }
      return Results.fail("Could not install JavaEE " + chosen.getSpecVersion());
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

}
