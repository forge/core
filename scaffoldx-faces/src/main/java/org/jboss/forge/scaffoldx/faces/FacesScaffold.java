/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffoldx.faces;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Entity;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.scaffoldx.AccessStrategy;
import org.jboss.forge.scaffoldx.ScaffoldProvider;
import org.jboss.forge.scaffoldx.TemplateStrategy;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.forge.spec.javaee.EJBFacet;
import org.jboss.forge.spec.javaee.FacesAPIFacet;
import org.jboss.forge.spec.javaee.PersistenceFacet;

/**
 * Scaffold-X provider for JSF scaffolding. Delegates to the original FacesScaffold provider supplied in the Forge 1.x
 * distribution.
 * 
 * @author VineetReynolds
 * @author Richard Kennard
 */
@Alias("faces")
@Help("JavaServer Faces scaffolding")
@RequiresFacet({ WebResourceFacet.class,
         DependencyFacet.class,
         PersistenceFacet.class,
         EJBFacet.class,
         CDIFacet.class,
         FacesAPIFacet.class })
public class FacesScaffold extends BaseFacet implements ScaffoldProvider
{

   @Inject
   private ShellPrintWriter writer;

   @Inject
   @Alias("faces")
   private org.jboss.forge.scaffold.faces.FacesScaffold delegate;
   
   @Inject
   private Configuration config;
   
   @Override
   public void setProject(Project project)
   {
      delegate.setProject(project);
      super.setProject(project);
   }
   
   @Override
   public boolean install()
   {
      return delegate.install();
   }

   @Override
   public boolean isInstalled()
   {
      return delegate.isInstalled();
   }

   @Override
   public List<Resource<?>> setup(String targetDir, boolean overwrite, boolean installTemplates)
   {
      synchronizeTargetDir(this, delegate);
      return delegate.setup(targetDir, null, overwrite);
   }

   @Override
   public List<Resource<?>> generateFrom(List<Resource<?>> resources, String targetDir, boolean overwrite)
   {
      synchronizeTargetDir(this, delegate);
      List<Resource<?>> generatedResources = new ArrayList<Resource<?>>();
      try
      {
         List<JavaClass> javaClasses = selectTargets(resources);
         for (JavaClass javaClass : javaClasses)
         {
            generatedResources.addAll(delegate.generateFromEntity(targetDir, null, javaClass, overwrite));
         }
      }
      catch (FileNotFoundException fileEx)
      {
         throw new RuntimeException(fileEx);
      }
      return generatedResources;
   }

   @Override
   public AccessStrategy getAccessStrategy()
   {
      // Return null, since everything is handled by the delegate
      return null;
   }

   @Override
   public TemplateStrategy getTemplateStrategy()
   {
      // Return null, since everything is handled by the delegate
      return null;
   }

   private List<JavaClass> selectTargets(List<Resource<?>> resources) throws FileNotFoundException
   {
      List<JavaClass> results = new ArrayList<JavaClass>();
      for (Resource<?> r : resources)
      {
         if (r instanceof JavaResource)
         {
            JavaSource<?> entity = ((JavaResource) r).getJavaSource();

            if (entity instanceof JavaClass)
            {
               if (entity.hasAnnotation(Entity.class))
               {
                  results.add((JavaClass) entity);
               }
               else
               {
                  displaySkippingResourceMsg(entity);
               }
            }
            else
            {
               displaySkippingResourceMsg(entity);
            }
         }
      }
      return results;
   }

   private void displaySkippingResourceMsg(final JavaSource<?> entity)
   {
      ShellMessages.info(writer, "Skipped non-@Entity Java resource [" + entity.getQualifiedName() + "]");
   }
   
   private void synchronizeTargetDir(FacesScaffold facesScaffold, org.jboss.forge.scaffold.faces.FacesScaffold delegate)
   {
      String targetDirKey = getTargetDirConfigKey(facesScaffold);
      String target = config.getString(targetDirKey);
      if (!Strings.isNullOrEmpty(target))
      {
         String delegateTargetDirConfigKey = getDelegateTargetDirConfigKey(delegate);
         config.setProperty(delegateTargetDirConfigKey, target);
      }
   }
   
   private String getTargetDirConfigKey(ScaffoldProvider provider)
   {
      return provider.getClass().getName() + "_targetDir";
   }
   
   private String getDelegateTargetDirConfigKey(org.jboss.forge.scaffold.ScaffoldProvider provider)
   {
      return provider.getClass().getName() + "_targetDir";
   }

}
