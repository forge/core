/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.spec.javaee.jpa;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.util.Refactory;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.util.ResourceUtil;
import org.jboss.forge.spec.javaee.PersistenceFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("entity")
@RequiresProject
@RequiresFacet(PersistenceFacet.class)
@Help("A plugin to manage simple @Entity and View creation; a basic MVC framework plugin.")
public class EntityPlugin implements Plugin
{
   private final Project project;

   private final Shell shell;

   @Inject
   public EntityPlugin(final Project project, final Shell shell)
   {
      this.project = project;
      this.shell = shell;
   }

   @SuppressWarnings("unchecked")
   @DefaultCommand(help = "Create a JPA @Entity")
   public void newEntity(
            @Option(required = true,
                     name = "named",
                     description = "The @Entity name") final String entityName,
            @Option(required = false,
                     name = "package",
                     type = PromptType.JAVA_PACKAGE,
                     description = "The package name") final String packageName) throws Throwable
   {
      final PersistenceFacet jpa = project.getFacet(PersistenceFacet.class);
      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      String entityPackage;

      if ((packageName != null) && !"".equals(packageName))
      {
         entityPackage = packageName;
      }
      else if (getPackagePortionOfCurrentDirectory() != null)
      {
         entityPackage = getPackagePortionOfCurrentDirectory();
      }
      else
      {
         entityPackage = shell.promptCommon(
                  "In which package you'd like to create this @Entity, or enter for default",
                  PromptType.JAVA_PACKAGE, jpa.getEntityPackage());
      }

      JavaClass javaClass = JavaParser.create(JavaClass.class)
               .setPackage(entityPackage)
               .setName(entityName)
               .setPublic()
               .addAnnotation(Entity.class).getOrigin()
               .addInterface(Serializable.class);

      javaClass.addField("private static final long serialVersionUID = 1L;");

      Field<JavaClass> id = javaClass.addField("private Long id = null;");
      id.addAnnotation(Id.class);
      id.addAnnotation(GeneratedValue.class)
               .setEnumValue("strategy", GenerationType.AUTO);
      id.addAnnotation(Column.class)
               .setStringValue("name", "id")
               .setLiteralValue("updatable", "false")
               .setLiteralValue("nullable", "false");

      Field<JavaClass> version = javaClass.addField("private int version = 0;");
      version.addAnnotation(Version.class);
      version.addAnnotation(Column.class).setStringValue("name", "version");

      Refactory.createGetterAndSetter(javaClass, id);
      Refactory.createGetterAndSetter(javaClass, version);
      Refactory.createToStringFromFields(javaClass, id);
      Refactory.createHashCodeAndEquals(javaClass);

      JavaResource javaFileLocation = java.saveJavaSource(javaClass);

      shell.println("Created @Entity [" + javaClass.getQualifiedName() + "]");

      /**
       * Pick up the generated resource.
       */
      shell.execute("pick-up " + javaFileLocation.getFullyQualifiedName());
   }

   /**
    * Retrieves the package portion of the current directory if it is a package, null otherwise.
    *
    * @return String representation of the current package, or null
    */
   private String getPackagePortionOfCurrentDirectory()
   {
      for (DirectoryResource r : project.getFacet(JavaSourceFacet.class).getSourceFolders())
      {
         final DirectoryResource currentDirectory = shell.getCurrentDirectory();
         if (ResourceUtil.isChildOf(r, currentDirectory))
         {
            // Have to remember to include the last slash so it's not part of the package
            return currentDirectory.getFullyQualifiedName().replace(r.getFullyQualifiedName() + "/", "")
                     .replaceAll("/", ".");
         }
      }
      return null;
   }
}
