/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.parser.java.utils.Packages;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.addon.resource.visit.ResourceVisit;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaPackageInfoSource;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint(MavenFacet.class)
@FacetConstraint(MavenJavaCompilerFacet.class)
public class MavenJavaSourceFacet extends AbstractFacet<Project>implements JavaSourceFacet
{
   @Override
   public List<DirectoryResource> getSourceDirectories()
   {
      List<DirectoryResource> result = new ArrayList<>();
      result.add(getSourceDirectory());
      result.add(getTestSourceDirectory());
      return result;
   }

   @Override
   public String calculateName(final JavaResource resource)
   {
      String fullPath = Packages.fromFileSyntax(resource.getFullyQualifiedName());
      String pkg = calculatePackage(resource);
      String name = fullPath.substring(fullPath.lastIndexOf(pkg) + pkg.length() + 1);
      name = name.substring(0, name.lastIndexOf(".java"));
      return name;
   }

   @Override
   public String calculatePackage(final JavaResource resource)
   {
      List<DirectoryResource> folders = getSourceDirectories();
      String pkg = null;
      for (DirectoryResource folder : folders)
      {
         String sourcePrefix = folder.getFullyQualifiedName();
         pkg = resource.getParent().getFullyQualifiedName();
         if (pkg.startsWith(sourcePrefix))
         {
            pkg = pkg.substring(sourcePrefix.length() + 1);
            break;
         }
      }
      pkg = Packages.fromFileSyntax(pkg);

      return pkg;
   }

   @Override
   public String getBasePackage()
   {
      Model model = getFaceted().getFacet(MavenFacet.class).getModel();
      String groupId = model.getGroupId();
      if (groupId == null)
      {
         groupId = model.getParent().getGroupId();
      }
      return Packages.toValidPackageName(groupId);
   }

   @Override
   public DirectoryResource getBasePackageDirectory()
   {
      return getSourceDirectory().getChildDirectory(Packages.toFileSyntax(getBasePackage()));
   }

   @Override
   public DirectoryResource getSourceDirectory()
   {
      MavenFacet mavenFacet = getFaceted().getFacet(MavenFacet.class);
      Build build = mavenFacet.getModel().getBuild();
      String srcFolderName;
      if (build != null && build.getSourceDirectory() != null)
      {
         srcFolderName = mavenFacet.resolveProperties(build.getSourceDirectory());
      }
      else
      {
         srcFolderName = "src" + File.separator + "main" + File.separator + "java";
      }
      DirectoryResource projectRoot = getFaceted().getRoot().reify(DirectoryResource.class);
      return projectRoot.getChildDirectory(srcFolderName);
   }

   @Override
   public DirectoryResource getTestSourceDirectory()
   {
      MavenFacet mavenFacet = getFaceted().getFacet(MavenFacet.class);
      Build build = mavenFacet.getModel().getBuild();
      String srcFolderName;
      if (build != null && build.getTestSourceDirectory() != null)
      {
         srcFolderName = mavenFacet.resolveProperties(build.getTestSourceDirectory());
      }
      else
      {
         srcFolderName = "src" + File.separator + "test" + File.separator + "java";
      }
      DirectoryResource projectRoot = getFaceted().getRoot().reify(DirectoryResource.class);
      return projectRoot.getChildDirectory(srcFolderName);
   }

   @Override
   public boolean isInstalled()
   {
      return getSourceDirectory().exists();
   }

   @Override
   public boolean install()
   {
      for (DirectoryResource folder : this.getSourceDirectories())
      {
         folder.mkdirs();
         savePackage(getBasePackage(), false);
      }
      return isInstalled();
   }

   @Override
   public JavaResource getJavaResource(final JavaSource<?> javaClass)
   {
      String pkg = Strings.isNullOrEmpty(javaClass.getPackage()) ? "" : javaClass.getPackage() + ".";
      return getJavaResource(pkg + javaClass.getName());
   }

   @Override
   public JavaResource getTestJavaResource(final JavaSource<?> javaClass)
   {
      String pkg = Strings.isNullOrEmpty(javaClass.getPackage()) ? "" : javaClass.getPackage() + ".";
      return getTestJavaResource(pkg + javaClass.getName());
   }

   @Override
   public JavaResource getJavaResource(final String relativePath)
   {
      return getJavaResource(getSourceDirectory(), relativePath);
   }

   @Override
   public JavaResource getTestJavaResource(final String relativePath)
   {
      return getJavaResource(getTestSourceDirectory(), relativePath);
   }

   private JavaResource getJavaResource(final DirectoryResource sourceDir, final String relativePath)
   {
      if (Strings.isNullOrEmpty(relativePath))
      {
         throw new ResourceException("Empty relative path");
      }
      String path = relativePath.trim().endsWith(".java")
               ? relativePath.substring(0, relativePath.lastIndexOf(".java")) : relativePath;

      path = Packages.toFileSyntax(path) + ".java";
      JavaResource target = sourceDir.getChildOfType(JavaResource.class, path);
      return target;
   }

   @Override
   public JavaResource saveJavaSource(final JavaSource<?> source)
   {
      return getJavaResource(source.getQualifiedName()).setContents(source);
   }

   @Override
   public JavaResource saveTestJavaSource(final JavaSource<?> source)
   {
      return getTestJavaResource(source.getQualifiedName()).setContents(source);
   }

   @Override
   public void visitJavaSources(final JavaResourceVisitor visitor)
   {
      new ResourceVisit(getSourceDirectory()).perform(visitor, new ResourceFilter()
      {
         @Override
         public boolean accept(Resource<?> resource)
         {
            return resource instanceof DirectoryResource;
         }
      }, new ResourceFilter()
      {

         @Override
         public boolean accept(Resource<?> type)
         {
            return type instanceof JavaResource;
         }
      });
   }

   @Override
   public void visitJavaTestSources(final JavaResourceVisitor visitor)
   {
      new ResourceVisit(getTestSourceDirectory()).perform(visitor, new ResourceFilter()
      {
         @Override
         public boolean accept(Resource<?> resource)
         {
            return resource instanceof DirectoryResource;
         }
      }, new ResourceFilter()
      {

         @Override
         public boolean accept(Resource<?> type)
         {
            return type instanceof JavaResource;
         }
      });
   }

   @Override
   public DirectoryResource savePackage(String packageName, boolean createPackageInfo)
   {
      DirectoryResource child = getPackage(packageName);
      if (!child.exists())
      {
         child.mkdirs();
      }
      if (createPackageInfo)
      {
         JavaPackageInfoSource packageInfo = Roaster.create(JavaPackageInfoSource.class).setPackage(packageName);
         JavaResource resource = child.getChild("package-info.java").reify(JavaResource.class);
         resource.setContents(packageInfo);
      }
      return child;
   }

   @Override
   public DirectoryResource saveTestPackage(String packageName, boolean createPackageInfo)
   {
      DirectoryResource child = getTestPackage(packageName);
      if (!child.exists())
      {
         child.mkdirs();
      }
      if (createPackageInfo)
      {
         JavaPackageInfoSource packageInfo = Roaster.create(JavaPackageInfoSource.class).setPackage(packageName);
         JavaResource resource = child.getChild("package-info.java").reify(JavaResource.class);
         resource.setContents(packageInfo);
      }
      return child;
   }

   @Override
   public DirectoryResource getPackage(String packageName)
   {
      return getSourceDirectory().getChildDirectory(packageName.replace('.', File.separatorChar));
   }

   @Override
   public DirectoryResource getTestPackage(String packageName)
   {
      return getTestSourceDirectory().getChildDirectory(packageName.replace('.', File.separatorChar));
   }

}
