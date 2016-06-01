/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.parser.java.converters.PackageRootConverter;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.DirectoryResource;
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
 * Command to create new Java packages
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(JavaSourceFacet.class)
public class JavaNewPackageCommandImpl extends AbstractProjectCommand implements JavaNewPackageCommand
{
   @Inject
   @WithAttributes(label = "Package Name", description = "The package name to be created in this project", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Create package-info.java", description = "Create package-info.java file in the new package")
   private UIInput<Boolean> createPackageInfo;

   @Inject
   @WithAttributes(label = "Create package in test directory", description = "Create package in test directory")
   private UIInput<Boolean> testFolder;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Java: New Package")
               .description("Creates a new package")
               .category(Categories.create("Java"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      named.setValueConverter(new PackageRootConverter(getProjectFactory(), builder));
      builder.add(named).add(createPackageInfo).add(testFolder);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      String packageName = named.getValue();
      JavaSourceFacet facet = getSelectedProject(context).getFacet(JavaSourceFacet.class);
      DirectoryResource newPackage;
      if (testFolder.getValue())
      {
         newPackage = facet.saveTestPackage(packageName, createPackageInfo.getValue());
      }
      else
      {
         newPackage = facet.savePackage(packageName, createPackageInfo.getValue());
      }
      context.getUIContext().setSelection(newPackage);
      return Results.success(String.format("Package '%s' created succesfully.", packageName));
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Inject
   private ProjectFactory projectFactory;

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }
}
