/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.facets;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface JavaSourceFacet extends ProjectFacet
{
   /**
    * Return the class name for the given {@link JavaResource} whether it exists or not.
    */
   String calculateName(JavaResource resource);

   /**
    * Return the package for the given {@link JavaResource} whether it exists or not.
    */
   String calculatePackage(JavaResource resource);

   /**
    * Return the base Java {@link Package} for this project, returned as a {@link String}
    */
   String getBasePackage();

   /**
    * Return the base Java {@link Package} for this project, returned as a {@link DirectoryResource}
    */
   DirectoryResource getBasePackageDirectory();

   /**
    * Get a list of {@link DirectoryResource}s this project uses to contain {@link Project} source documents (such as
    * .java files.)
    */
   List<DirectoryResource> getSourceDirectories();

   /**
    * Get the {@link DirectoryResource} this {@link Project} uses to store package-able source documents (such as .java
    * files.)
    */
   DirectoryResource getSourceDirectory();

   /**
    * Get the {@link DirectoryResource} this {@link Project} uses to store test-scoped source documents (such as .java
    * files.) Files in this directory will never be packaged or deployed except when running Unit Tests.
    */
   DirectoryResource getTestSourceDirectory();

   /**
    * Create or update a Java file in the primary source directory: {@link #getSourceDirectory()} - use information in
    * the given {@link org.jboss.forge.roaster.model.source.JavaSource} to determine the appropriate package; packages
    * will be created if necessary.
    *
    * @param source The java class to create
    * @return The created or updated {@link JavaResource}
    * @throws FileNotFoundException
    */
   JavaResource saveJavaSource(JavaSource<?> source);

   /**
    * Create or update a Java file <b>without formatting</b> in the primary source directory:
    * {@link #getSourceDirectory()} - use information in the given
    * {@link org.jboss.forge.roaster.model.source.JavaSource} to determine the appropriate package; packages will be
    * created if necessary.
    * 
    * @param source
    * @return
    */
   default JavaResource saveJavaSourceUnformatted(JavaSource<?> source)
   {
      JavaResource javaResource = getJavaResource(source);
      javaResource.setContents(new ByteArrayInputStream(source.toUnformattedString().getBytes()), null);
      return javaResource;
   }

   /**
    * Create or update a Java file in the primary test source directory: {@link #getTestSourceDirectory()} - use
    * information in the given {@link org.jboss.forge.roaster.model.source.JavaSource} to determine the appropriate
    * package; packages will be created if necessary.
    *
    * @param source The java class to create
    * @return The created or updated {@link JavaResource}
    */
   JavaResource saveTestJavaSource(JavaSource<?> source);

   /**
    * Create or update a Java file <b>without formatting</b> in the primary test source directory:
    * {@link #getTestSourceDirectory()} - use information in the given
    * {@link org.jboss.forge.roaster.model.source.JavaSource} to determine the appropriate package; packages will be
    * created if necessary.
    *
    * @param source The java class to create
    * @return The created or updated {@link JavaResource}
    */
   default JavaResource saveTestJavaSourceUnformatted(JavaSource<?> source)
   {
      JavaResource javaResource = getTestJavaResource(source);
      javaResource.setContents(new ByteArrayInputStream(source.toUnformattedString().getBytes()), null);
      return javaResource;
   }

   /**
    * Return the {@link JavaClass} at the given path relative to {@link #getSourceDirectory()}.
    *
    * @param relativePath The file or package path of the target Java source file.
    * @throws FileNotFoundException if the target {@link JavaResource} does not exist
    */
   JavaResource getJavaResource(String relativePath);

   /**
    * Attempt to locate and re-parse the given {@link JavaSource} from its location on disk, relative to
    * {@link #getSourceDirectory()}. The given instance will not be modified, and a new instance will be returned.
    *
    * @param javaClass The {@link JavaClass} to re-parse.
    * @throws FileNotFoundException if the target {@link JavaResource} does not exist
    */
   JavaResource getJavaResource(JavaSource<?> javaClass);

   /**
    * Return the {@link JavaClass} at the given path relative to {@link #getTestSourceDirectory()}.
    *
    * @param relativePath The package path of the target Java source {@link JavaResource}.
    */
   JavaResource getTestJavaResource(String relativePath);

   /**
    * Attempt to locate and re-parse the given {@link JavaClass} from its location on disk, relative to
    * {@link #getTestSourceDirectory()}. The given instance will not be modified, and a new instance will be returned.
    *
    * @param javaClass The {@link JavaClass} to re-parse.
    * @throws FileNotFoundException if the target {@link JavaResource} does not exist
    */
   JavaResource getTestJavaResource(JavaSource<?> javaClass);

   /**
    * Recursively loops over all the source directories and for each java file it finds, calls the visitor.
    *
    * @param visitor The {@link JavaResourceVisitor} that processes all the found java files. Cannot be null.
    */
   void visitJavaSources(JavaResourceVisitor visitor);

   /**
    * Recursively loops over all the test source directories and for each java file it finds, calls the visitor.
    *
    * @param visitor The {@link JavaResourceVisitor} that processes all the found java files. Cannot be null.
    */
   void visitJavaTestSources(JavaResourceVisitor visitor);

   /**
    * Create a package in the specified path under the {@link DirectoryResource} returned in
    * {@link #getSourceDirectory()}
    * 
    * @param packageName the package name to be created
    * @param createPackageInfo create a package-info.java file under this package?
    * @return a {@link DirectoryResource} with the path for the new package
    */
   DirectoryResource savePackage(String packageName, boolean createPackageInfo);

   /**
    * Create a package in the specified path under the {@link DirectoryResource} returned in
    * {@link #getTestSourceDirectory()}
    * 
    * @param packageName the package name to be created
    * @param createPackageInfo create a package-info.java file under this package?
    * @return a {@link DirectoryResource} with the path for the new package
    */
   DirectoryResource saveTestPackage(String packageName, boolean createPackageInfo);

   /**
    * Return the package in the specified path under the {@link DirectoryResource} returned in
    * {@link #getSourceDirectory()}
    * 
    * @param packageName the package name to be created
    * @return a {@link DirectoryResource} with the package path
    */
   DirectoryResource getPackage(String packageName);

   /**
    * Returns the package in the specified path under the {@link DirectoryResource} returned in
    * {@link #getTestSourceDirectory()}
    * 
    * @param packageName the package name to be created
    * @return a {@link DirectoryResource} with the package path
    */
   DirectoryResource getTestPackage(String packageName);

}
