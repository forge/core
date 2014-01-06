/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.addons;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.jboss.forge.addon.parser.java.JavaSourceFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;

/**
 * Creates a Furnace Test case
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class NewFurnaceTestCommand extends AbstractProjectCommand
{

   @Inject
   private JavaSourceFactory javaSourceFactory;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private Furnace furnace;

   @Inject
   @WithAttributes(label = "Package Name", type = InputType.JAVA_PACKAGE_PICKER)
   private UIInput<String> packageName;

   @Inject
   @WithAttributes(label = "Test Class Name", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Dependency addons", description = "Addons this test depends upon")
   private UISelectMany<AddonId> addonDependencies;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Set<AddonId> choices = new TreeSet<>();
      for (AddonRepository repository : furnace.getRepositories())
      {
         // Avoid immutable repositories
         if (repository instanceof MutableAddonRepository)
         {
            for (AddonId id : repository.listEnabled())
            {
               choices.add(id);
            }
         }
      }
      addonDependencies.setValueChoices(choices);
      Project project = getSelectedProject(builder.getUIContext());
      packageName.setDefaultValue(project.getFacet(MetadataFacet.class).getTopLevelPackage());
      builder.add(packageName).add(named).add(addonDependencies);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.AbstractUICommand#getMetadata(org.jboss.forge.addon.ui.context.UIContext)
    */
   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Addon: New Test Case")
               .description("Generates a Furnace test case for an addon")
               .category(Categories.create("Addon", "Generate"));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaClass javaClass = javaSourceFactory.create(JavaClass.class).setName(named.getValue())
               .setPackage(packageName.getValue());

      // Add imports
      javaClass.addImport("org.jboss.arquillian.container.test.api.Deployment");
      javaClass.addImport("org.jboss.arquillian.junit.Arquillian");
      javaClass.addImport("org.junit.runner.RunWith");
      javaClass.addImport("org.jboss.forge.arquillian.AddonDependency");
      javaClass.addImport("org.jboss.forge.arquillian.Dependencies");
      javaClass.addImport("org.jboss.forge.arquillian.archive.ForgeArchive");
      javaClass.addImport("org.jboss.forge.furnace.repositories.AddonDependencyEntry");
      javaClass.addImport("org.jboss.shrinkwrap.api.ShrinkWrap");

      // Add Arquillian annotation
      javaClass.addAnnotation("RunWith").setLiteralValue("Arquillian.class");

      // Create getDeployment method
      StringBuilder body = new StringBuilder(
               "ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class).addBeansXML()");
      StringBuilder dependenciesAnnotationBody = new StringBuilder();
      if (addonDependencies.hasValue())
      {
         body.append(".addAsAddonDependencies(");
         Iterator<AddonId> it = addonDependencies.getValue().iterator();
         while (it.hasNext())
         {
            AddonId addonId = it.next();
            String name = addonId.getName();
            body.append("AddonDependencyEntry.create(\"").append(name).append("\")");
            dependenciesAnnotationBody.append("@AddonDependency(name = \"").append(name).append("\")");
            if (it.hasNext())
            {
               body.append(",");
               dependenciesAnnotationBody.append(",");
            }
         }
         body.append(")");
      }
      body.append(";");
      body.append("return archive;");
      Method<JavaClass> getDeployment = javaClass.addMethod().setName("getDeployment").setPublic().setStatic(true)
               .setBody(body.toString());
      getDeployment.addAnnotation("Deployment");
      String annotationBody = dependenciesAnnotationBody.toString();
      if (annotationBody.length() > 0)
      {
         getDeployment.addAnnotation("Dependencies").setLiteralValue("{" + annotationBody + "}");
      }

      UIContext uiContext = context.getUIContext();
      Project project = getSelectedProject(uiContext);
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.saveTestJavaSource(javaClass);
      uiContext.setSelection(javaResource);
      return Results.success("Test class " + javaClass.getQualifiedName() + " created");
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
