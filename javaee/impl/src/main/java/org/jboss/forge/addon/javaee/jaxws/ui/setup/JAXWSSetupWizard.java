/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jaxws.ui.setup;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.facets.JAXWSFacet;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Setups JAX-WS in a {@link Project}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint(DependencyFacet.class)
public class JAXWSSetupWizard extends AbstractJavaEECommand
{

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("JAX-WS: Setup")
               .description("Setup JAX-WS in your project")
               .category(Categories.create(super.getMetadata(context).getCategory(), "JAX-WS"));
   }

   @Inject
   private FacetFactory facetFactory;

   @Inject
   @WithAttributes(required = true, label = "JAX-WS Version")
   private UISelectOne<JAXWSFacet> jaxwsVersion;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      jaxwsVersion.setItemLabelConverter(new Converter<JAXWSFacet, String>()
      {
         @Override
         public String convert(JAXWSFacet source)
         {
            return source.getSpecVersion().toString();
         }
      });

      for (JAXWSFacet choice : jaxwsVersion.getValueChoices())
      {
         if (jaxwsVersion.getValue() == null
                  || choice.getSpecVersion().compareTo(jaxwsVersion.getValue().getSpecVersion()) >= 1)
         {
            jaxwsVersion.setDefaultValue(choice);
         }
      }

      builder.add(jaxwsVersion);
   }

   @Override
   public Result execute(final UIContext context) throws Exception
   {
      if (facetFactory.install(getSelectedProject(context), jaxwsVersion.getValue()))
      {
         return Results.success("JAX-WS has been installed.");
      }
      return Results.fail("Could not install JAX-WS.");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

}
