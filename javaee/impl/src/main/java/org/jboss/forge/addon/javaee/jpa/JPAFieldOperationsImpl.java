/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.jboss.forge.addon.parser.java.beans.FieldOperations;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.util.Refactory;
import org.jboss.forge.roaster.model.util.Types;

/**
 * Operations in the New Field wizard
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class JPAFieldOperationsImpl extends FieldOperations implements JPAFieldOperations
{

   /**
    * Creates a One-to-One relationship
    * 
    * @param project
    * @param resource
    * @param fieldName
    * @param fieldType
    * @param inverseFieldName
    * @param fetchType
    * @param required
    * @param cascadeTypes
    * @throws FileNotFoundException
    */
   @Override
   public void newOneToOneRelationship(Project project, final JavaResource resource, final String fieldName,
            final String fieldType,
            final String inverseFieldName,
            final FetchType fetchType, final boolean required,
            final Iterable<CascadeType> cascadeTypes) throws FileNotFoundException
   {
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClassSource entityClass = getJavaClassFrom(resource);
      JavaClassSource fieldEntityClass;
      if (areTypesSame(fieldType, entityClass.getCanonicalName()))
      {
         fieldEntityClass = entityClass;
      }
      else
      {
         fieldEntityClass = findEntity(project, fieldType);
         entityClass.addImport(fieldEntityClass);
      }

      FieldSource<JavaClassSource> localField = addFieldTo(entityClass, fieldEntityClass.getName(), fieldName,
               OneToOne.class.getName());
      AnnotationSource<JavaClassSource> annotation = localField.getAnnotation(OneToOne.class);
      if ((inverseFieldName != null) && !inverseFieldName.isEmpty())
      {
         FieldSource<JavaClassSource> inverseField = addFieldTo(fieldEntityClass, entityClass.getName(),
                  inverseFieldName,
                  OneToOne.class.getName());
         inverseField.getAnnotation(OneToOne.class).setStringValue("mappedBy", localField.getName());
         java.saveJavaSource(fieldEntityClass);
      }

      if (fetchType != null && fetchType != FetchType.EAGER)
      {
         annotation.setEnumValue("fetch", fetchType);
      }
      if (required)
      {
         // Set the optional attribute of @OneToOne/@ManyToOne only when false, since the default value is true
         annotation.setLiteralValue("optional", "false");
      }
      addCascade(cascadeTypes, annotation);
      java.saveJavaSource(entityClass);
   }

   private void addCascade(final Iterable<CascadeType> cascadeTypes, AnnotationSource<JavaClassSource> annotation)
   {
      if (cascadeTypes != null)
      {
         List<CascadeType> cascades = new ArrayList<>();
         for (CascadeType cascade : cascadeTypes)
         {
            cascades.add(cascade);
         }
         if (!cascades.isEmpty())
         {
            // If all cascades selected, use CascadeType.ALL
            if (cascades.containsAll(EnumSet.range(CascadeType.PERSIST, CascadeType.DETACH)))
            {
               cascades.clear();
               cascades.add(CascadeType.ALL);
            }
            annotation.setEnumArrayValue("cascade", cascades.toArray(new CascadeType[cascades.size()]));
         }
      }
   }

   /**
    * Creates a Many-To-One relationship
    * 
    * @param project
    * @param resource
    * @param fieldName
    * @param fieldType
    * @param inverseFieldName
    * @param fetchType
    * @param required
    * @param cascadeTypes
    * @throws FileNotFoundException
    */
   @Override
   public void newManyToOneRelationship(
            final Project project,
            final JavaResource resource,
            final String fieldName,
            final String fieldType,
            final String inverseFieldName,
            final FetchType fetchType,
            final boolean required,
            final Iterable<CascadeType> cascadeTypes) throws FileNotFoundException
   {
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClassSource many = getJavaClassFrom(resource);
      JavaClassSource one;
      if (areTypesSame(fieldType, many.getCanonicalName()))
      {
         one = many;
      }
      else
      {
         one = findEntity(project, fieldType);
         many.addImport(one);
      }
      if (many.hasField(fieldName))
      {
         throw new IllegalStateException("Entity [" + many.getCanonicalName() + "] already has a field named ["
                  + fieldName + "]");
      }
      if (!Strings.isNullOrEmpty(inverseFieldName) && one.hasField(inverseFieldName))
      {
         throw new IllegalStateException("Entity [" + one.getCanonicalName() + "] already has a field named ["
                  + inverseFieldName + "]");
      }

      FieldSource<JavaClassSource> manyField = many.addField("private " + one.getName() + " " + fieldName + ";");
      AnnotationSource<JavaClassSource> manyAnnotation = manyField.addAnnotation(ManyToOne.class);
      Refactory.createGetterAndSetter(many, manyField);

      if (!Strings.isNullOrEmpty(inverseFieldName))
      {
         one.addImport(Set.class);
         one.addImport(HashSet.class);
         if (!one.getCanonicalName().equals(many.getCanonicalName()))
         {
            one.addImport(many.getQualifiedName());
         }
         FieldSource<JavaClassSource> oneField = one.addField("private Set<" + many.getName() + "> " + inverseFieldName
                  + "= new HashSet<"
                  + many.getName() + ">();");
         AnnotationSource<JavaClassSource> oneAnnotation = oneField.addAnnotation(OneToMany.class).setStringValue(
                  "mappedBy",
                  fieldName);
         oneAnnotation.setLiteralValue("cascade", "CascadeType.ALL");
         oneAnnotation.getOrigin().addImport(CascadeType.class);

         Refactory.createGetterAndSetter(one, oneField);
         java.saveJavaSource(one);
      }

      if (fetchType != null && fetchType != FetchType.EAGER)
      {
         manyAnnotation.setEnumValue("fetch", fetchType);
      }
      if (required)
      {
         // Set the optional attribute of @OneToOne/@ManyToOne only when false, since the default value is true
         manyAnnotation.setLiteralValue("optional", "false");
      }
      addCascade(cascadeTypes, manyAnnotation);
      java.saveJavaSource(many);
   }

   /**
    * Creates an Embedded relationship
    * 
    * @param project
    * @param resource
    * @param fieldName
    * @param fieldType
    * @throws FileNotFoundException
    */
   @Override
   public void newEmbeddedRelationship(
            final Project project,
            final JavaResource resource,
            final String fieldName,
            final String fieldType) throws FileNotFoundException
   {
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClassSource entityClass = getJavaClassFrom(resource);
      JavaClassSource fieldEntityClass;
      if (areTypesSame(fieldType, entityClass.getCanonicalName()))
      {
         fieldEntityClass = entityClass;
      }
      else
      {
         fieldEntityClass = findEntity(project, fieldType);
         entityClass.addImport(fieldEntityClass);
      }

      addFieldTo(entityClass, fieldEntityClass.getName(), fieldName,
               Embedded.class.getName());
      java.saveJavaSource(entityClass);
   }

   @Override
   public void newOneToManyRelationship(
            final Project project,
            final JavaResource resource,
            final String fieldName,
            final String fieldType,
            final String inverseFieldName,
            final FetchType fetchType,
            final Iterable<CascadeType> cascadeTypes)
            throws FileNotFoundException
   {
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClassSource one = getJavaClassFrom(resource);

      JavaClassSource many;
      // Field type may end with .java
      if (areTypesSame(fieldType, one.getCanonicalName()))
      {
         many = one;
      }
      else
      {
         many = findEntity(project, fieldType);
         one.addImport(many.getQualifiedName());
      }

      if (one.hasField(fieldName))
      {
         throw new IllegalStateException("Entity [" + one.getCanonicalName() + "] already has a field named ["
                  + fieldName + "]");
      }
      if (!Strings.isNullOrEmpty(inverseFieldName) && many.hasField(inverseFieldName))
      {
         throw new IllegalStateException("Entity [" + many.getCanonicalName() + "] already has a field named ["
                  + inverseFieldName + "]");
      }

      one.addImport(Set.class);
      one.addImport(HashSet.class);

      FieldSource<JavaClassSource> oneField = one.addField("private Set<" + many.getName() + "> " + fieldName
               + "= new HashSet<"
               + many.getName() + ">();");
      AnnotationSource<JavaClassSource> annotation = oneField.addAnnotation(OneToMany.class);
      Refactory.createGetterAndSetter(one, oneField);

      if (!Strings.isNullOrEmpty(inverseFieldName))
      {
         annotation.setStringValue("mappedBy", inverseFieldName);
         annotation.setLiteralValue("cascade", "CascadeType.ALL");
         annotation.getOrigin().addImport(CascadeType.class);
         annotation.setLiteralValue("orphanRemoval", "true");
         if (!many.getCanonicalName().equals(one.getCanonicalName()))
         {
            many.addImport(one);
         }
         FieldSource<JavaClassSource> manyField = many.addField("private " + one.getName() + " " + inverseFieldName
                  + ";");
         manyField.addAnnotation(ManyToOne.class);
         Refactory.createGetterAndSetter(many, manyField);
         java.saveJavaSource(many);
      }

      if (fetchType != null && fetchType != FetchType.LAZY)
      {
         annotation.setEnumValue("fetch", fetchType);
      }
      addCascade(cascadeTypes, annotation);
      java.saveJavaSource(one);
   }

   @Override
   public void newManyToManyRelationship(
            final Project project,
            final JavaResource resource,
            final String fieldName,
            final String fieldType,
            final String inverseFieldName,
            final FetchType fetchType,
            final Iterable<CascadeType> cascadeTypes) throws FileNotFoundException
   {

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClassSource entity = getJavaClassFrom(resource);
      JavaClassSource otherEntity;
      if (areTypesSame(fieldType, entity.getCanonicalName()))
      {
         otherEntity = entity;
      }
      else
      {
         otherEntity = findEntity(project, fieldType);
         entity.addImport(otherEntity.getQualifiedName());
      }

      if (entity.hasField(fieldName))
      {
         throw new IllegalStateException("Entity [" + entity.getCanonicalName() + "] already has a field named ["
                  + fieldName + "]");
      }
      if (!Strings.isNullOrEmpty(inverseFieldName) && otherEntity.hasField(inverseFieldName))
      {
         throw new IllegalStateException("Entity [" + otherEntity.getCanonicalName()
                  + "] already has a field named ["
                  + inverseFieldName + "]");
      }

      entity.addImport(Set.class);
      entity.addImport(HashSet.class);
      FieldSource<JavaClassSource> field = entity.addField("private Set<" + otherEntity.getName() + "> " + fieldName
               + "= new HashSet<"
               + otherEntity.getName() + ">();");
      AnnotationSource<JavaClassSource> annotation = field.addAnnotation(ManyToMany.class);
      Refactory.createGetterAndSetter(entity, field);

      if (!Strings.isNullOrEmpty(inverseFieldName))
      {
         annotation.setStringValue("mappedBy", inverseFieldName);

         otherEntity.addImport(Set.class);
         otherEntity.addImport(HashSet.class);
         if (!otherEntity.getCanonicalName().equals(entity.getCanonicalName()))
         {
            otherEntity.addImport(entity.getQualifiedName());
         }
         FieldSource<JavaClassSource> otherField = otherEntity.addField("private Set<" + entity.getName() + "> "
                  + inverseFieldName
                  + "= new HashSet<" + entity.getName() + ">();");
         otherField.addAnnotation(ManyToMany.class);
         Refactory.createGetterAndSetter(otherEntity, otherField);

         java.saveJavaSource(otherEntity);
      }

      if (fetchType != null && fetchType != FetchType.LAZY)
      {
         annotation.setEnumValue("fetch", fetchType);
      }
      addCascade(cascadeTypes, annotation);
      java.saveJavaSource(entity);
   }

   private JavaClassSource findEntity(Project project, final String entity) throws FileNotFoundException
   {
      JavaClassSource result = null;

      JPAFacet<?> persistence = project.getFacet(JPAFacet.class);
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      if (entity != null)
      {
         result = getJavaClassFrom(java.getJavaResource(entity));
         if (result == null)
         {
            result = getJavaClassFrom(java.getJavaResource(persistence.getEntityPackage() + "." + entity));
         }
      }

      if (result == null)
      {
         throw new FileNotFoundException("Could not locate JavaClass on which to operate.");
      }

      return result;
   }

   private JavaClassSource getJavaClassFrom(final Resource<?> resource) throws FileNotFoundException
   {
      JavaSource<?> source = ((JavaResource) resource).getJavaType();
      if (!(source instanceof JavaClassSource))
      {
         throw new IllegalStateException("Current resource is not a JavaClass!");
      }
      return (JavaClassSource) source;
   }

   /**
    * Checks if the types are the same, removing the ".java" in the end of the string in case it exists
    * 
    * @param from
    * @param to
    * @return
    */
   private boolean areTypesSame(String from, String to)
   {
      String fromCompare = from.endsWith(".java") ? from.substring(0, from.length() - 5) : from;
      String toCompare = to.endsWith(".java") ? to.substring(0, to.length() - 5) : to;
      return fromCompare.equals(toCompare);
   }

   @Override
   protected boolean canAddFieldToToString(Field<JavaClassSource> field)
   {
      return super.canAddFieldToToString(field)
               && (field.getType().isPrimitive() || Types.isJavaLang(field.getType().getQualifiedName()))
               && !"id".equals(field.getName())
               && !"version".equals(field.getName())
               && !field.hasAnnotation(Transient.class);
   }

}
