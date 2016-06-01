/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jstl.ui.setup;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.jstl.JSTLFacet;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
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
 * Setups JSTL in a {@link Project}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint(DependencyFacet.class)
public class JSTLSetupWizard extends AbstractJavaEECommand
{

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("JSTL: Setup")
               .description("Setup JSTL in your project")
               .category(Categories.create(super.getMetadata(context).getCategory(), "JSTL"));
   }

   @Inject
   private FacetFactory facetFactory;

   @Inject
   @WithAttributes(required = true, label = "JSTL Version")
   private UISelectOne<JSTLFacet> jstlVersion;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      jstlVersion.setItemLabelConverter(new Converter<JSTLFacet, String>()
      {
         @Override
         public String convert(JSTLFacet source)
         {
            return source.getSpecVersion().toString();
         }
      });

      for (JSTLFacet choice : jstlVersion.getValueChoices())
      {
         if (jstlVersion.getValue() == null
                  || choice.getSpecVersion().compareTo(jstlVersion.getValue().getSpecVersion()) >= 1)
         {
            jstlVersion.setDefaultValue(choice);
         }
      }

      builder.add(jstlVersion);
   }

   @Override
   public Result execute(final UIExecutionContext context) throws Exception
   {
      if (facetFactory.install(getSelectedProject(context), jstlVersion.getValue()))
      {
         return Results.success("JSTL has been installed.");
      }
      return Results.fail("Could not install JSTL.");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }
}