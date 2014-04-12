package org.jboss.forge.addon.parser.java.ui;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet.*;

/**
 *
 * @author <a href="ivan.st.ivanov@gmail.com">Ivan St. Ivanov</a>
 */
@FacetConstraint(JavaCompilerFacet.class)
public class SetCompilerVersionCommand extends AbstractProjectCommand
{
   @Inject
   @WithAttributes(label = "Java sources version")
   private UISelectOne<JavaCompilerFacet.CompilerVersion> sourceVersion;

   @Inject
   @WithAttributes(label = "Target compilation version")
   private UISelectOne<JavaCompilerFacet.CompilerVersion> targetVersion;

   @Override public UICommandMetadata getMetadata(UIContext context)
   {
      return  Metadata.from(super.getMetadata(context), getClass()).name("Project: Set Compiler Version")
               .description("Set the java sources and the target compilation version")
               .category(Categories.create(super.getMetadata(context).getCategory(), "Project"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      List<JavaCompilerFacet.CompilerVersion> values = Arrays.asList(CompilerVersion.values());
      sourceVersion.setValueChoices(values);
      targetVersion.setValueChoices(values);
      builder.add(sourceVersion).add(targetVersion);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaCompilerFacet facet = getJavaCompilerFacet(context);

      CompilerVersion sourceVersion = this.sourceVersion.getValue() == null ? facet.getSourceCompilerVersion() : this.sourceVersion.getValue();
      CompilerVersion targetVersion = this.targetVersion.getValue() == null ? facet.getTargetCompilerVersion() : this.targetVersion.getValue();

      if (sourceVersion.ordinal() > targetVersion.ordinal())
      {
         return Results.fail("Selected source version (" + sourceVersion.toString() +
                  ") is higher than the target version (" + targetVersion.toString() + ").");
      }

      facet.setSourceCompilerVersion(sourceVersion);
      facet.setTargetCompilerVersion(targetVersion);
      return Results.success();
   }

   private JavaCompilerFacet getJavaCompilerFacet(UIExecutionContext context)
   {
      Project project = getSelectedProject(context);

      return project.getFacet(JavaCompilerFacet.class);
   }

   @Override protected boolean isProjectRequired()
   {
      return true;
   }

   @Inject
   private ProjectFactory projectFactory;

   @Override protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

}
