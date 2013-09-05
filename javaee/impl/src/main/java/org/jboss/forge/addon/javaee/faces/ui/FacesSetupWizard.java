/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.faces.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.faces.FacesFacet;
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
 * Setups EJB in a {@link Project}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint(DependencyFacet.class)
public class FacesSetupWizard extends AbstractJavaEECommand
{

   @Override
   public Metadata getMetadata()
   {
      return Metadata.from(super.getMetadata(), getClass()).name("Faces: Setup")
               .description("Setup JavaServer Faces in your project")
               .category(Categories.create(super.getMetadata().getCategory(), "JSF"));
   }

   @Inject
   private FacetFactory facetFactory;

   @Inject
   @WithAttributes(required = true, label = "JavaServer Faces Version")
   private UISelectOne<FacesFacet> choices;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      choices.setItemLabelConverter(new Converter<FacesFacet, String>()
      {
         @Override
         public String convert(FacesFacet source)
         {
            return source.getSpecVersion().toString();
         }
      });

      for (FacesFacet choice : choices.getValueChoices())
      {
         if (choices.getValue() == null || choice.getSpecVersion().compareTo(choices.getValue().getSpecVersion()) >= 1)
         {
            choices.setDefaultValue(choice);
         }
      }

      builder.add(choices);
   }

   @Override
   public Result execute(final UIContext context) throws Exception
   {
      if (facetFactory.install(getSelectedProject(context), choices.getValue()))
      {
         return Results.success("JavaServer Faces has been installed.");
      }
      return Results.fail("Could not install JavaServer Faces.");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

}
