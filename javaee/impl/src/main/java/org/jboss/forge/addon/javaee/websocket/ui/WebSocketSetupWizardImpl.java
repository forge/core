/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.websocket.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.javaee.websocket.WebSocketFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraint;
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
 * Setups WebSockets in a {@link Project}
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(DependencyFacet.class)
@StackConstraint(WebSocketFacet.class)
public class WebSocketSetupWizardImpl extends AbstractJavaEECommand implements WebSocketSetupWizard
{

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("WebSocket: Setup")
               .description("Setup WebSocket API in your project")
               .category(Categories.create(super.getMetadata(context).getCategory(), "WebSocket"));
   }

   @Inject
   private FacetFactory facetFactory;

   @Inject
   @WithAttributes(required = true, label = "WebSocket Version", defaultValue = "1.0")
   private UISelectOne<WebSocketFacet> webSocketVersion;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(webSocketVersion);
   }

   @Override
   public Result execute(final UIExecutionContext context) throws Exception
   {
      WebSocketFacet facet = webSocketVersion.getValue();
      if (facetFactory.install(getSelectedProject(context), facet))
      {
         return Results.success(String.format("WebSocket API %s has been installed.", facet.getSpecVersion()));
      }
      return Results.fail(String.format("Could not install WebSocket API %s", facet.getSpecVersion()));
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

}
