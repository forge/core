/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jpa;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Type;
import org.jboss.forge.parser.java.util.Refactory;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.spec.javaee.PersistenceFacet;

@Alias("update-entity")
@RequiresProject
@RequiresFacet(PersistenceFacet.class)
@Help("A plugin to aid in refactoring of JPA @Entity classes.")
public class UpdateEntityPlugin implements Plugin
{

   @Inject
   @Current
   private Resource<?> currentResource;

   @Inject
   private Project project;

   @Inject
   private ShellPrintWriter writer;

   @Inject
   private Shell shell;

   @Command(value = "hashcode-and-equals", help = "Create or updates the hashCode() and equals() methods for JPA @Entities")
   public void createOrUpdateHashCodeAndEquals(
            @Option(required = false,
                     description = "The JPA @Entity classes") JavaResource[] resources) throws Throwable
   {
      if (((resources == null) || (resources.length < 1)) && (currentResource instanceof JavaResource))
      {
         resources = new JavaResource[] { (JavaResource) currentResource };
      }

      List<JavaResource> entities = selectEntities(resources);
      if (entities.isEmpty())
      {
         ShellMessages.error(writer, "Must specify atleast one @Entity class on which to operate.");
         return;
      }

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      for (JavaResource resource : resources)
      {
         JavaClass entity = (JavaClass) (resource).getJavaSource();

         Set<Field<?>> selectedFields = new HashSet<Field<?>>();
         Map<String, Field<?>> candidateFields = getCandidateFields(entity);
         selectedFields = shell.promptMultiSelectWithWildcard("*",
                  "Choose the fields to use in the equals and hashCode methods. Use * to select all.",
                  candidateFields);
         warnIncorrectFields(entity, selectedFields);
         overwriteMethods(entity);
         Refactory.createHashCodeAndEquals(entity, selectedFields.toArray(new Field<?>[selectedFields.size()]));
         java.saveJavaSource(entity);
         shell.println("Added equals() and hashCode() in [" + entity.getQualifiedName() + "].");
      }
   }

   private void overwriteMethods(JavaClass entity)
   {
      if (entity.hasMethodSignature("equals", Object.class))
      {
         entity.removeMethod(entity.getMethod("equals", Object.class));
      }
      if (entity.hasMethodSignature("hashCode"))
      {
         entity.removeMethod(entity.getMethod("hashCode"));
      }
   }

   private Map<String, Field<?>> getCandidateFields(JavaClass entity)
   {
      Map<String, Field<?>> candidateFields = new HashMap<String,Field<?>>();
      for (Field<?> field : entity.getFields())
      {
         // Skip static fields
         if (field.isStatic())
         {
            continue;
         }

         candidateFields.put(field.getName(),field);
      }
      return candidateFields;
   }

   private List<JavaResource> selectEntities(Resource<?>[] targets) throws FileNotFoundException
   {
      List<JavaResource> results = new ArrayList<JavaResource>();
      for (Resource<?> r : targets)
      {
         if (r instanceof JavaResource)
         {
            JavaSource<?> entity = ((JavaResource) r).getJavaSource();

            if (entity instanceof JavaClass)
            {
               if (entity.hasAnnotation(Entity.class))
               {
                  results.add((JavaResource) r);
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

   private void warnIncorrectFields(JavaClass entity, Set<Field<?>> selectedFields)
   {
      for (Field<?> field : selectedFields)
      {
         Type<?> typeInspector = field.getTypeInspector();
         String fieldType = typeInspector.getQualifiedName();
         
         // TODO: Warn if the field is of a type that implements equals() and hashCode(). This will however depend on
         // the ability to obtain a JavaClass instance for the type of the field.
         if (field.isTransient() || field.hasAnnotation(Transient.class))
         {
            displayTransientFieldWarningMsg(entity, field);
         }
         if (fieldType.equals("java.util.Collection") || fieldType.equals("java.util.List")
                  || fieldType.equals("java.util.Set") || fieldType.equals("java.util.Map"))
         {
            displayCollectionFieldWarningMsg(entity, field);
         }
         // Warn for JPA @GeneratedValue @Version fields
         if (field.hasAnnotation(GeneratedValue.class))
         {
            displayGeneratedValueFieldWarningMsg(entity, field);
         }
         if(field.hasAnnotation(Version.class))
         {
            displayVersionFieldWarningMsg(entity, field);
         }
      }
   }
   
   private void displayGeneratedValueFieldWarningMsg(JavaClass klass, Field<?> field)
   {
      ShellMessages.warn(writer, "A field [" + field.getName()
               + "] having the @GeneratedValue annotation was chosen. The generated equals() and hashCode() methods for the class [" + klass.getName()
               + "] may be incorrect.");
   }
   
   private void displayVersionFieldWarningMsg(JavaClass klass, Field<?> field)
   {
      ShellMessages.warn(writer, "A field [" + field.getName()
               + "] having the @Version annotation was chosen. The generated equals() and hashCode() methods for the class [" + klass.getName()
               + "] may be incorrect.");
   }

   private void displayCollectionFieldWarningMsg(JavaClass klass, Field<?> field)
   {
      ShellMessages.warn(writer, "A collection field [" + field.getName()
               + "] was chosen. The generated equals() and hashCode() methods for the class [" + klass.getName()
               + "] may be incorrect.");
   }

   private void displayTransientFieldWarningMsg(JavaClass klass, Field<?> field)
   {
      ShellMessages.warn(writer, "A transient field [" + field.getName()
               + "] was chosen. The generated equals() and hashCode() methods for the class [" + klass.getName()
               + "] may be incorrect.");
   }

   private void displaySkippingResourceMsg(final JavaSource<?> entity)
   {
      ShellMessages.info(writer, "Skipped non-@Entity Java resource [" + entity.getQualifiedName() + "]");
   }

}
