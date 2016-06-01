/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="ivan.st.ivanov@gmail.com">Ivan St. Ivanov</a>
 */
@FacetConstraint(JavaCompilerFacet.class)
public class SetCompilerVersionCommandImpl extends AbstractProjectCommand implements SetCompilerVersionCommand
{
   @Inject
   @WithAttributes(label = "Java sources version", required = true)
   private UISelectOne<JavaCompilerFacet.CompilerVersion> sourceVersion;

   @Inject
   @WithAttributes(label = "Target compilation version", required = true)
   private UISelectOne<JavaCompilerFacet.CompilerVersion> targetVersion;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("Project: Set Compiler Version")
               .description("Set the java sources and the target compilation version")
               .category(Categories.create("Project", "Manage"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      JavaCompilerFacet javaCompilerFacet = getJavaCompilerFacet(builder.getUIContext());
      sourceVersion.setDefaultValue(javaCompilerFacet.getSourceCompilerVersion());
      targetVersion.setDefaultValue(javaCompilerFacet.getTargetCompilerVersion());
      builder.add(sourceVersion).add(targetVersion);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaCompilerFacet facet = getJavaCompilerFacet(context.getUIContext());

      facet.setSourceCompilerVersion(sourceVersion.getValue());
      facet.setTargetCompilerVersion(targetVersion.getValue());
      return Results.success();
   }

   private JavaCompilerFacet getJavaCompilerFacet(UIContext context)
   {
      Project project = getSelectedProject(context);

      return project.getFacet(JavaCompilerFacet.class);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      if (sourceVersion.getValue() != null && targetVersion.getValue() != null)
      {
         if (sourceVersion.getValue().ordinal() > targetVersion.getValue().ordinal())
         {
            validator.addValidationError(sourceVersion, "Selected source version (" + sourceVersion.getValue() +
                     ") is higher than the target version (" + targetVersion.getValue() + ").");
         }
      }
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Inject
   private ProjectFactory projectFactory;

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

}
