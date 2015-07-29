package org.jboss.forge.addon.javaee.security.ui;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.ui.command.PrerequisiteCommandsProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

import javax.inject.Inject;

/**
 * Adds a security role to the current project.
 *
 * @author <a href="mailto:ivan.st.ivanov@gmail.com">Ivan St. Ivanov</a>
 */
@FacetConstraint(ServletFacet.class)
public class AddSecurityRoleCommand extends AbstractJavaEECommand
{

   @Inject
   @WithAttributes(label = "Role name", required = true, requiredMessage = "You should enter the role name")
   private UIInput<String> roleName;

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(roleName);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      ServletFacet servletFacet = getSelectedProject(context).getFacet(ServletFacet.class);
      String roleName = this.roleName.getValue();
      servletFacet.addSecurityRole(roleName);
      return Results.success("Security role " + roleName + " was created");
   }


   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("Security: Add Role")
               .description("Add security role")
               .category(Categories.create(super.getMetadata(context).getCategory().getName(), "Security"));
   }

}
