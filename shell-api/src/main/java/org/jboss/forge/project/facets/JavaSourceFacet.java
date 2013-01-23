/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.facets;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.resources.java.JavaResourceVisitor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface JavaSourceFacet extends Facet
{

   /**
    * Return the class name for the given {@link JavaResource} whether it exists or not.
    */
   String calculateName(JavaResource resource);

   /**
    * Return the package for the given {@link JavaResource} whether it exists or not.
    */
   public String calculatePackage(JavaResource resource);

   /**
    * Return the base Java {@link Package} for this project, returned as a {@link String}
    */
   public String getBasePackage();

   /**
    * Return the base Java {@link Package} for this project, returned as a directory {@link File}
    */
   public DirectoryResource getBasePackageResource();

   /**
    * Get a list of {@link DirectoryResource}s this project uses to contain {@link Project} source documents (such as
    * .java files.)
    */
   public List<DirectoryResource> getSourceFolders();

   /**
    * Get the {@link DirectoryResource} this {@link Project} uses to store package-able source documents (such as .java
    * files.)
    */
   public DirectoryResource getSourceFolder();

   /**
    * Get the {@link DirectoryResource} this {@link Project} uses to store test-scoped source documents (such as .java
    * files.) Files in this directory will never be packaged or deployed except when running Unit Tests.
    */
   public DirectoryResource getTestSourceFolder();

   /**
    * Create or update a Java file in the primary source directory: {@link #getSourceFolder()} - use information in the
    * given {@link JavaSource} to determine the appropriate package; packages will be created if necessary.
    *
    * @param source The java class to create
    * @return The created or updated {@link JavaResource}
    * @throws FileNotFoundException
    */
   public JavaResource saveJavaSource(JavaSource<?> source) throws FileNotFoundException;

   /**
    * Create or update a Java file in the primary source directory: {@link #getSourceFolder()} - use information in the
    * given {@link JavaEnum} to determine the appropriate package; packages will be created if necessary.
    *
    * @param source The java enum type to create
    * @return The created or updated {@link EnumTypeResource}
    * @throws FileNotFoundException
    *
    * @deprecated Use {@link JavaSourceFacet#saveJavaSource(JavaSource)}
    */
   @Deprecated
   public JavaResource saveEnumTypeSource(final JavaEnum source) throws FileNotFoundException;

   /**
    * Create or update a Java file in the primary test source directory: {@link #getTestSourceFolder()} - use
    * information in the given {@link JavaSource} to determine the appropriate package; packages will be created if
    * necessary.
    *
    * @param source The java class to create
    * @return The created or updated {@link JavaResource}
    */
   public JavaResource saveTestJavaSource(JavaSource<?> source) throws FileNotFoundException;

   /**
    * Return the {@link JavaClass} at the given path relative to {@link #getSourceFolder()}.
    *
    * @param relativePath The file or package path of the target Java source file.
    * @throws FileNotFoundException if the target {@link JavaResource} does not exist
    */
   public JavaResource getJavaResource(String relativePath) throws FileNotFoundException;

   /**
    * Attempt to locate and re-parse the given {@link JavaClass} from its location on disk, relative to
    * {@link #getSourceFolder()}. The given instance will not be modified, and a new instance will be returned.
    *
    * @param javaClass The {@link JavaClass} to re-parse.
    * @throws FileNotFoundException if the target {@link JavaResource} does not exist
    */
   public JavaResource getJavaResource(JavaSource<?> javaClass) throws FileNotFoundException;

   /**
    * Return the {@link JavaEnum} at the given path relative to {@link #getSourceFolder()}.
    *
    * @param relativePath The file or package path of the target Java source file.
    * @throws FileNotFoundException if the target {@link JavaResource} does not exist
    *
    * @deprecated Use {@link JavaSourceFacet#getJavaResource(String} instead
    */
   @Deprecated
   public JavaResource getEnumTypeResource(String relativePath) throws FileNotFoundException;

   /**
    * Attempt to locate and re-parse the given {@link JavaEnum} from its location on disk, relative to
    * {@link #getSourceFolder()}. The given instance will not be modified, and a new instance will be returned.
    *
    * @param javaClass The {@link JavaClass} to re-parse.
    * @throws FileNotFoundException if the target {@link JavaResource} does not exist
    *
    * @deprecated Use {@link JavaSourceFacet#getJavaResource(JavaSource)} instead
    */
   @Deprecated
   public JavaResource getEnumTypeResource(JavaEnum javaEnum) throws FileNotFoundException;

   /**
    * Return the {@link JavaClass} at the given path relative to {@link #getTestSourceFolder()}.
    *
    * @param relativePath The package path of the target Java source {@link JavaResource}.
    */
   public JavaResource getTestJavaResource(String relativePath) throws FileNotFoundException;

   /**
    * Attempt to locate and re-parse the given {@link JavaClass} from its location on disk, relative to
    * {@link #getTestSourceFolder()}. The given instance will not be modified, and a new instance will be returned.
    *
    * @param javaClass The {@link JavaClass} to re-parse.
    * @throws FileNotFoundException if the target {@link JavaResource} does not exist
    */
   public JavaResource getTestJavaResource(JavaSource<?> javaClass) throws FileNotFoundException;

   /**
    * Recursively loops over all the source directories and for each java file it finds, calls the visitor.
    *
    * @param visitor The {@link JavaResourceVisitor} that processes all the found java files. Cannot be null.
    */
   public void visitJavaSources(JavaResourceVisitor visitor);

   /**
    * Recursively loops over all the test source directories and for each java file it finds, calls the visitor.
    *
    * @param visitor The {@link JavaResourceVisitor} that processes all the found java files. Cannot be null.
    */
   public void visitJavaTestSources(JavaResourceVisitor visitor);

}
