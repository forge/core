/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.ui.setup;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.rest.RestFacet;
import org.jboss.forge.addon.javaee.rest.config.RestConfigurationStrategy;
import org.jboss.forge.addon.javaee.rest.config.RestConfigurationStrategyFactory;
import org.jboss.forge.addon.javaee.rest.ui.RestSetupWizard;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraint;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * Setups JAX-RS in a project
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(JavaSourceFacet.class)
@StackConstraint(RestFacet.class)
public class RestSetupWizardImpl extends AbstractJavaEECommand implements RestSetupWizard
{
   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("REST: Setup")
               .description("Setup REST in your project")
               .category(Categories.create(super.getMetadata(context).getCategory(), "JAX-RS"));
   }

   @Inject
   private FacetFactory facetFactory;

   @Inject
   @WithAttributes(required = true, label = "JAX-RS Version", defaultValue = "1.1")
   private UISelectOne<RestFacet> jaxrsVersion;

   @Inject
   @WithAttributes(required = true, label = "Configuration Strategy", type = InputType.RADIO)
   private UISelectOne<RestActivatorType> config;

   @Inject
   @WithAttributes(label = "Application Path", description = "The Application path for the REST resources", defaultValue = "/rest", required = true)
   private UIInput<String> applicationPath;

   @Inject
   @WithAttributes(label = "Target Package", type = InputType.JAVA_PACKAGE_PICKER)
   private UIInput<String> targetPackage;

   @Inject
   @WithAttributes(label = "Class Name", defaultValue = "RestApplication")
   private UIInput<String> className;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Project project = getSelectedProject(builder);
      if (filterValueChoicesFromStack(project, jaxrsVersion))
      {
         builder.add(jaxrsVersion);
      }
      configureActivationStrategy(builder.getUIContext(), project);

      builder.add(applicationPath).add(config).add(targetPackage).add(className);
   }

   private void configureActivationStrategy(UIContext context, Project project)
   {
      config.setDefaultValue(RestActivatorType.APP_CLASS);
      Callable<Boolean> appClassChosen = new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return RestActivatorType.APP_CLASS == config.getValue();
         }
      };
      if (context.getProvider().isGUI())
      {
         config.setItemLabelConverter(new Converter<RestActivatorType, String>()
         {
            @Override
            public String convert(RestActivatorType source)
            {
               return source != null ? source.getDescription() : null;
            }
         });
      }
      targetPackage.setRequired(appClassChosen).setEnabled(appClassChosen);
      className.setRequired(appClassChosen).setEnabled(appClassChosen);
      targetPackage.setDefaultValue(project.getFacet(JavaSourceFacet.class).getBasePackage() + ".rest");
   }

   @Override
   public Result execute(final UIExecutionContext context) throws Exception
   {
      RestFacet facet = jaxrsVersion.getValue();
      if (facetFactory.install(getSelectedProject(context), facet))
      {
         String path = applicationPath.getValue();
         final RestConfigurationStrategy strategy;
         if (config.getValue() == RestActivatorType.WEB_XML)
         {
            strategy = RestConfigurationStrategyFactory.createUsingWebXml(path);
         }
         else
         {
            JavaClassSource javaClass = Roaster.create(JavaClassSource.class).setPackage(targetPackage.getValue())
                     .setName(className.getValue());
            strategy = RestConfigurationStrategyFactory.createUsingJavaClass(path, javaClass);
         }
         facet.setConfigurationStrategy(strategy);
         return Results.success("JAX-RS has been installed.");
      }
      return Results.fail("Could not install JAX-RS.");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

}
