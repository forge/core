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
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

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
   private UIInput<String> named;

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(named);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      ServletFacet<?> servletFacet = getSelectedProject(context).getFacet(ServletFacet.class);
      String roleName = this.named.getValue();
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
