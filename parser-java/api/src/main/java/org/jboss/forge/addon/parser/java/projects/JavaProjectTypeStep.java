/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.projects;


import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class JavaProjectTypeStep extends AbstractUICommand implements UIWizardStep
{

   @Inject
   @WithAttributes(label = "Create Main Class", description = "Toggle creation of a simple Main() script in the root package, valid for jar projects only")
   private UIInput<Boolean> createMain;

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      return null;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Java Project Type Setup")
               .description("Information setup for the Java project type");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(createMain);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Map<Object, Object> attributeMap = context.getUIContext().getAttributeMap();
      Project project = (Project) attributeMap.get(Project.class);
      if (project.hasFacet(JavaSourceFacet.class))
      {
         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         MetadataFacet metadata = project.getFacet(MetadataFacet.class);
         if (createMain.getValue())
         {
            JavaClassSource javaClass = Roaster
                     .create(JavaClassSource.class)
                     .setPackage(facet.getBasePackage())
                     .setName("Main")
                     .addMethod("public static void main(String[] args) {}")
                     .setBody("System.out.println(\"Hi there! I was forged as part of the project you call "
                              + metadata.getProjectName()
                              + ".\");")
                     .getOrigin();
            facet.saveJavaSource(javaClass);
         }
      }

      return null;
   }
}
