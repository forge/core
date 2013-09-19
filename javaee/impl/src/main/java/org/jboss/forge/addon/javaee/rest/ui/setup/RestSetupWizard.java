/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.rest.ui.setup;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.rest.RestFacet;
import org.jboss.forge.addon.javaee.rest.config.RestConfigurationStrategy;
import org.jboss.forge.addon.javaee.rest.config.RestConfigurationStrategyFactory;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;

/**
 * Setups JAX-RS in a project
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RestSetupWizard extends AbstractJavaEECommand
{
   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("Rest: Setup")
               .description("Setup REST in your project")
               .category(Categories.create(super.getMetadata(context).getCategory(), "JAX-RS"));
   }

   @Inject
   private FacetFactory facetFactory;

   @Inject
   @WithAttributes(required = true, label = "JAX-RS Version")
   private UISelectOne<RestFacet> versions;

   @Inject
   @WithAttributes(required = true, label = "Configuration Strategy", type = InputType.RADIO)
   private UISelectOne<RestActivatorType> config;

   @Inject
   @WithAttributes(label = "Application Path", description = "The Application path for the REST resources", defaultValue = "/rest", required = true)
   private UIInput<String> applicationPath;

   @Inject
   @WithAttributes(label = "Package Name", type = InputType.JAVA_PACKAGE_PICKER)
   private UIInput<String> packageName;

   @Inject
   @WithAttributes(label = "Class Name", defaultValue = "RestApplication")
   private UIInput<String> className;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      configureVersions();
      configureActivationStrategy(builder.getUIContext());
      builder.add(versions).add(applicationPath).add(config).add(packageName).add(className);
   }

   private void configureActivationStrategy(UIContext context)
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
      config.setItemLabelConverter(new Converter<RestActivatorType, String>()
      {
         @Override
         public String convert(RestActivatorType source)
         {
            return source != null ? source.getDescription() : null;
         }
      });
      packageName.setRequired(appClassChosen).setEnabled(appClassChosen);
      className.setRequired(appClassChosen).setEnabled(appClassChosen);
      Project project = getSelectedProject(context);
      packageName.setDefaultValue(project.getFacet(MetadataFacet.class).getTopLevelPackage() + ".rest");
   }

   private void configureVersions()
   {
      versions.setItemLabelConverter(new Converter<RestFacet, String>()
      {
         @Override
         public String convert(RestFacet source)
         {
            return source.getSpecVersion().toString();
         }
      });

      for (RestFacet choice : versions.getValueChoices())
      {
         if (versions.getValue() == null
                  || choice.getSpecVersion().compareTo(versions.getValue().getSpecVersion()) >= 1)
         {
            versions.setDefaultValue(choice);
         }
      }
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
   }

   @Override
   public Result execute(final UIContext context) throws Exception
   {
      RestFacet facet = versions.getValue();
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
            JavaClass javaClass = JavaParser.create(JavaClass.class).setPackage(packageName.getValue())
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
