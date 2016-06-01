/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.security.ui;

import java.util.Arrays;
import java.util.List;

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
 * Adds a login config to the current project.
 *
 * @author <a href="mailto:ivan.st.ivanov@gmail.com">Ivan St. Ivanov</a>
 */
@FacetConstraint(ServletFacet.class)
public class AddLoginConfigCommand extends AbstractJavaEECommand
{

   @Inject
   @WithAttributes(label = "Security realm")
   private UIInput<String> securityRealm;

   @Inject
   @WithAttributes(label = "Authentication method", required = true)
   private UIInput<String> authMethod;

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      List<String> loginMethods = Arrays.asList("BASIC", "DIGEST", "FORM", "CLIENT_CERT");
      authMethod.setCompleter((context, input, value) -> loginMethods);
      builder.add(securityRealm).add(authMethod);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      ServletFacet<?> servletFacet = getSelectedProject(context).getFacet(ServletFacet.class);
      servletFacet.addLoginConfig(authMethod.getValue(), securityRealm.getValue());
      return Results.success("Security realm " + securityRealm.getValue() + " and authentication method " +
               authMethod.getValue() + " were configured");
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("Security: Add Login Config")
               .description("Adds a login config element to the current project")
               .category(Categories.create(super.getMetadata(context).getCategory().getName(), "Security"));
   }
}
