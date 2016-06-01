/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons.ui;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

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
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.manager.maven.addon.MavenAddonDependencyResolver;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.AnnotationSource;
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

   private UIInput<String> packageName;
   private UIInput<String> named;
   private UIInput<Boolean> reuseProjectAddons;
   private UISelectOne<AddonId> furnaceContainer;
   private UISelectMany<AddonId> addonDependencies;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      packageName = factory.createInput("packageName", String.class).setLabel("Package Name");
      packageName.getFacet(HintsFacet.class).setInputType(InputType.JAVA_PACKAGE_PICKER);
      named = factory.createInput("named", String.class).setLabel("Test Class Name").setRequired(true);
      reuseProjectAddons = factory.createInput("reuseProjectAddons", Boolean.class)
               .setLabel("Use Addons from current project as dependencies (automatic discovery)")
               .setDescription(
                        "This will create an empty @AddonDependencies and reuse the addons in the current project's pom.xml")
               .setDefaultValue(true);
      furnaceContainer = factory.createSelectOne("furnaceContainer", AddonId.class).setLabel("Furnace container")
               .setRequiredMessage("You must select one Furnace container");
      addonDependencies = factory.createSelectMany("addonDependencies", AddonId.class).setLabel("Dependency addons")
               .setDescription("Addons this test depends upon");
      configureAddonDependencies();
      Project project = getSelectedProject(builder.getUIContext());
      packageName.setDefaultValue(project.getFacet(JavaSourceFacet.class).getBasePackage());
      builder.add(packageName).add(named).add(reuseProjectAddons).add(furnaceContainer).add(addonDependencies);
   }

   private void configureAddonDependencies()
   {
      Set<AddonId> addonChoices = new TreeSet<>();
      Set<AddonId> containerChoices = new TreeSet<>();
      AddonId defaultContainer = null;
      AddonId defaultDependency = null;
      Furnace furnace = SimpleContainer.getFurnace(getClass().getClassLoader());
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
      // Enable addon dependencies
      Callable<Boolean> reuseProjectDepsCallable = new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return !reuseProjectAddons.getValue();
         }
      };
      furnaceContainer.setEnabled(reuseProjectDepsCallable).setRequired(reuseProjectDepsCallable);
      addonDependencies.setEnabled(reuseProjectDepsCallable);
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
      javaClass.addImport("org.jboss.forge.arquillian.AddonDependencies");
      javaClass.addImport("org.jboss.forge.arquillian.archive.AddonArchive");
      javaClass.addImport("org.jboss.shrinkwrap.api.ShrinkWrap");
      javaClass.addImport("org.junit.runner.RunWith");
      javaClass.addImport("org.junit.Assert");
      javaClass.addImport("org.junit.Test");

      // Add Arquillian annotation
      javaClass.addAnnotation("RunWith").setLiteralValue("Arquillian.class");

      // Create getDeployment method
      MethodSource<JavaClassSource> getDeployment = javaClass.addMethod().setName("getDeployment").setPublic()
               .setStatic(true)
               .setBody("return ShrinkWrap.create(AddonArchive.class).addBeansXML();").setReturnType("AddonArchive");
      getDeployment.addAnnotation("Deployment");
      AnnotationSource<JavaClassSource> addonDependenciesAnn = getDeployment.addAnnotation("AddonDependencies");

      if (!reuseProjectAddons.getValue())
      {
         StringBuilder dependenciesAnnotationBody = new StringBuilder();
         AddonId furnaceContainerId = furnaceContainer.getValue();
         addAddonDependency(project, dependenciesAnnotationBody, furnaceContainerId);
         Iterator<AddonId> it = addonDependencies.getValue().iterator();
         if (it.hasNext())
         {
            dependenciesAnnotationBody.append(",");
         }
         while (it.hasNext())
         {
            AddonId addonId = it.next();
            addAddonDependency(project, dependenciesAnnotationBody, addonId);
            if (it.hasNext())
            {
               dependenciesAnnotationBody.append(",");
            }
         }

         String annotationBody = dependenciesAnnotationBody.toString();
         if (annotationBody.length() > 0)
         {
            addonDependenciesAnn.setLiteralValue("{" + annotationBody + "}");
         }
      }

      // Create test method
      javaClass.addMethod().setName("testAddon").setPublic().setReturnTypeVoid()
               .setBody("Assert.fail(\"Not yet implemented\");").addAnnotation("Test");

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.saveTestJavaSource(javaClass);
      uiContext.setSelection(javaResource);
      return Results.success("Test class " + javaClass.getQualifiedName() + " created");
   }

   private void addAddonDependency(Project project, StringBuilder dependenciesAnnotationBody,
            AddonId addonId)
   {
      DependencyInstaller dependencyInstaller = SimpleContainer
               .getServices(getClass().getClassLoader(), DependencyInstaller.class).get();
      Dependency dependency = DependencyBuilder.create(addonId.getName()).setVersion(
               addonId.getVersion().toString()).setClassifier(MavenAddonDependencyResolver.FORGE_ADDON_CLASSIFIER)
               .setScopeType("test");
      String name = addonId.getName();
      if (!dependencyInstaller.isInstalled(project, dependency))
      {
         dependencyInstaller.install(project, dependency);
      }
      dependenciesAnnotationBody.append("@AddonDependency(name = \"").append(name).append("\")");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }

}
