/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.security.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
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

/**
 * Removes a security role from the current project.
 *
 * @author <a href="mailto:ivan.st.ivanov@gmail.com">Ivan St. Ivanov</a>
 */
@FacetConstraint(ServletFacet.class)
public class RemoveSecurityRoleCommand extends AbstractJavaEECommand
{
   @Inject
   @WithAttributes(label = "Role to remove", required = true, requiredMessage = "You should enter the role to be removed")
   private UISelectOne<String> named;

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      named.setValueChoices(getServletFacet(builder.getUIContext()).getSecurityRoles());
      builder.add(named);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      String roleToBeRemoved = named.getValue();
      if (getServletFacet(context.getUIContext()).removeSecurityRole(roleToBeRemoved))
         return Results.success("Role " + roleToBeRemoved + " was removed successfully");
      else
         return Results.fail("Role " + roleToBeRemoved + " could not be removed. Probably it does not exist");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return super.isEnabled(context) && getServletFacet(context).getSecurityRoles().size() > 0;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("Security: Remove Role")
               .description("Remove security role")
               .category(Categories.create(super.getMetadata(context).getCategory().getName(), "Security"));
   }

   private ServletFacet<?> getServletFacet(UIContext uiContext)
   {
      return getSelectedProject(uiContext).getFacet(ServletFacet.class);
   }

}
