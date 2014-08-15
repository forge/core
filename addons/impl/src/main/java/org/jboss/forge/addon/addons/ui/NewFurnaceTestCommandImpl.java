/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.addons.ui;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.jboss.forge.addon.addons.facets.AddonTestFacet;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.manager.maven.addon.MavenAddonDependencyResolver;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

/**
 * Creates a Furnace Test case
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(AddonTestFacet.class)
public class NewFurnaceTestCommandImpl extends AbstractProjectCommand implements NewFurnaceTestCommand
{
   private static final String DEFAULT_CONTAINER_NAME = "org.jboss.forge.furnace.container:cdi";
   private static final String DEFAULT_DEPENDENCY_NAME = "org.jboss.forge.addon:core";

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private Furnace furnace;

   @Inject
   private DependencyInstaller dependencyInstaller;

   @Inject
   @WithAttributes(label = "Package Name", type = InputType.JAVA_PACKAGE_PICKER)
   private UIInput<String> packageName;

   @Inject
   @WithAttributes(label = "Test Class Name", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Furnace container", required = true, requiredMessage = "You must select one Furnace container")
   private UISelectOne<AddonId> furnaceContainer;

   @Inject
   @WithAttributes(label = "Dependency addons", description = "Addons this test depends upon")
   private UISelectMany<AddonId> addonDependencies;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      configureAddonDependencies();
      Project project = getSelectedProject(builder.getUIContext());
      packageName.setDefaultValue(project.getFacet(JavaSourceFacet.class).getBasePackage());
      builder.add(packageName).add(named).add(furnaceContainer).add(addonDependencies);
   }

   private void configureAddonDependencies()
   {
      Set<AddonId> addonChoices = new TreeSet<>();
      Set<AddonId> containerChoices = new TreeSet<>();
      AddonId defaultContainer = null;
      AddonId defaultDependency = null;
      for (AddonRepository repository : furnace.getRepositories())
      {
         for (AddonId id : repository.listEnabled())
         {
            if (DEFAULT_CONTAINER_NAME.equals(id.getName()))
            {
               defaultContainer = id;
            }
            else if (DEFAULT_DEPENDENCY_NAME.equals(id.getName()))
            {
               defaultDependency = id;
            }
            // TODO: Furnace should provide some way to detect if an addon is a Container type
            boolean isContainerAddon = id.getName().contains("org.jboss.forge.furnace.container");
            if (isContainerAddon)
            {
               containerChoices.add(id);
            }
            else
            {
               addonChoices.add(id);
            }
         }
      }
      furnaceContainer.setValueChoices(containerChoices).setDefaultValue(defaultContainer);
      addonDependencies.setValueChoices(addonChoices);
      if (defaultDependency != null)
         addonDependencies.setDefaultValue(Arrays.asList(defaultDependency));
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Addon: New Test")
               .description("Generates a Furnace test case for an addon")
               .category(Categories.create("Forge", "Generate"));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      Project project = getSelectedProject(uiContext);
      JavaClassSource javaClass = Roaster.create(JavaClassSource.class).setName(named.getValue())
               .setPackage(packageName.getValue());

      // Add imports
      javaClass.addImport("org.jboss.arquillian.container.test.api.Deployment");
      javaClass.addImport("org.jboss.arquillian.junit.Arquillian");
      javaClass.addImport("org.jboss.forge.arquillian.AddonDependency");
      javaClass.addImport("org.jboss.forge.arquillian.Dependencies");
      javaClass.addImport("org.jboss.forge.arquillian.archive.ForgeArchive");
      javaClass.addImport("org.jboss.forge.furnace.repositories.AddonDependencyEntry");
      javaClass.addImport("org.jboss.shrinkwrap.api.ShrinkWrap");
      javaClass.addImport("org.junit.runner.RunWith");

      // Add Arquillian annotation
      javaClass.addAnnotation("RunWith").setLiteralValue("Arquillian.class");

      // Create getDeployment method
      StringBuilder body = new StringBuilder(
               "ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class).addBeansXML()");
      StringBuilder dependenciesAnnotationBody = new StringBuilder();
      body.append(".addAsAddonDependencies(");
      AddonId furnaceContainerId = furnaceContainer.getValue();
      addAddonDependency(project, body, dependenciesAnnotationBody, furnaceContainerId);
      Iterator<AddonId> it = addonDependencies.getValue().iterator();
      if (it.hasNext())
      {
         body.append(",");
         dependenciesAnnotationBody.append(",");
      }
      while (it.hasNext())
      {
         AddonId addonId = it.next();
         addAddonDependency(project, body, dependenciesAnnotationBody, addonId);
         if (it.hasNext())
         {
            body.append(",");
            dependenciesAnnotationBody.append(",");
         }
      }
      body.append(")");
      body.append(";");
      body.append("return archive;");
      MethodSource<JavaClassSource> getDeployment = javaClass.addMethod().setName("getDeployment").setPublic()
               .setStatic(true)
               .setBody(body.toString()).setReturnType("ForgeArchive");
      getDeployment.addAnnotation("Deployment");
      String annotationBody = dependenciesAnnotationBody.toString();
      if (annotationBody.length() > 0)
      {
         getDeployment.addAnnotation("Dependencies").setLiteralValue("{" + annotationBody + "}");
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.saveTestJavaSource(javaClass);
      uiContext.setSelection(javaResource);
      return Results.success("Test class " + javaClass.getQualifiedName() + " created");
   }

   private void addAddonDependency(Project project, StringBuilder body, StringBuilder dependenciesAnnotationBody,
            AddonId addonId)
   {
      Dependency dependency = DependencyBuilder.create(addonId.getName()).setVersion(
               addonId.getVersion().toString()).setClassifier(MavenAddonDependencyResolver.FORGE_ADDON_CLASSIFIER).setScopeType("test");
      String name = addonId.getName();
      if (!dependencyInstaller.isInstalled(project, dependency))
      {
         dependencyInstaller.install(project, dependency);
      }
      body.append("AddonDependencyEntry.create(\"").append(name);
      dependenciesAnnotationBody.append("@AddonDependency(name = \"").append(name);
      body.append("\")");
      dependenciesAnnotationBody.append("\")");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

}
