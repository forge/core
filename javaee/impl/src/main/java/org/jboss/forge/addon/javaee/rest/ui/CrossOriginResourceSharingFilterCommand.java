/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.rest.ui;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.jboss.forge.addon.javaee.rest.RestFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.ui.AbstractJavaSourceCommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.command.PrerequisiteCommandsProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.furnace.util.Lists;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

/**
 * Creates a Cross Origin Resource Sharing Filter
 * 
 * @see https://issues.jboss.org/browse/FORGE-1929
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class CrossOriginResourceSharingFilterCommand extends AbstractJavaSourceCommand<JavaClassSource> implements
         PrerequisiteCommandsProvider
{

   @Inject
   @WithAttributes(label = "Access-Control-Allow-Origin", defaultValue = "*")
   private UIInput<String> accessControlAllowOrigin;

   @Inject
   @WithAttributes(label = "Access-Control-Allow-Methods")
   private UISelectMany<String> accessControlAllowMethods;

   @Inject
   @WithAttributes(label = "Access-Control-Allow-Headers", defaultValue = "content-type")
   private UIInput<String> accessControlAllowHeaders;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      getNamed().setDefaultValue("NewCrossOriginResourceSharingFilter");
      accessControlAllowMethods.setValueChoices(Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
               HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS));
      accessControlAllowMethods.setDefaultValue(Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
               HttpMethod.DELETE));
      builder.add(accessControlAllowOrigin).add(accessControlAllowMethods).add(accessControlAllowHeaders);
   }

   @Override
   protected String getType()
   {
      return "Cross Origin Resource Sharing Filter";
   }

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }

   @Override
   protected String calculateDefaultPackage(UIContext context)
   {
      Project project = getSelectedProject(context);
      return project.getFacet(JavaSourceFacet.class).getBasePackage() + ".rest";
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      source.addAnnotation(Provider.class);
      source.addInterface(ContainerResponseFilter.class);
      MethodSource<JavaClassSource> method = source.addMethod().setName("filter").setPublic().setReturnTypeVoid();
      method.addAnnotation(Override.class);
      // FIXME java.lang.Override shouldn't be imported
      source.removeImport(Override.class);
      method.addParameter(ContainerRequestContext.class, "request");
      method.addParameter(ContainerResponseContext.class, "response");
      StringBuilder body = new StringBuilder();
      body.append("response.getHeaders().putSingle(\"Access-Control-Allow-Origin\",\"").append(
               accessControlAllowOrigin.getValue()).append("\");");
      body.append(OperatingSystemUtils.getLineSeparator());
      body.append("response.getHeaders().putSingle(\"Access-Control-Allow-Methods\",\"");
      List<String> accessMethods = Lists.toList(accessControlAllowMethods.getValue());
      body.append(Strings.join(accessMethods.toArray(), ", "));
      body.append("\");");
      body.append(OperatingSystemUtils.getLineSeparator());
      body.append("response.getHeaders().putSingle(\"Access-Control-Allow-Headers\",\"").append(
               accessControlAllowHeaders.getValue()).append("\");");
      method.setBody(body.toString());
      return source;
   }

   @Override
   public NavigationResult getPrerequisiteCommands(UIContext context)
   {
      NavigationResultBuilder builder = NavigationResultBuilder.create();
      Project project = getSelectedProject(context);
      if (!project.hasFacet(RestFacet.class))
      {
         builder.add(RestSetupWizard.class);
      }
      return builder.build();
   }
}
