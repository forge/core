/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa;

import java.io.FileNotFoundException;

import javax.persistence.GenerationType;

import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;

/**
 * Defines JPA specific operations
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface PersistenceOperations
{
   public static final String DEFAULT_UNIT_SUFFIX = "-persistence-unit";
   public static final String DEFAULT_UNIT_DESC = "Forge Persistence Unit";

   /**
    * Setups JPA in the project for the given attributes. If persistence unit with the name already exists, the unit
    * will be overwritten.
    * 
    * @param unitName Name of the created Persistence Unit
    * @param project Project for which the JPA is being set up
    * @param dataSource JPA data-source configuration
    * @param configureMetadata configure the metadata
    */
   public FileResource<?> setup(String unitName, Project project, JPADataSource dataSource, boolean configureMetadata);

   /**
    * Get the object representing the persistence unit, null is returned when no persistence unit was found.
    * 
    * @param unitName Name of the Persistence Unit
    * @param project Project in which the JPA is being looked for
    */
   @SuppressWarnings({ "rawtypes" })
   public PersistenceUnitCommon getExistingPersistenceUnit(Project project, String unitName);

   /**
    * Creates a new {@link JavaResource} in the specified project. If no project is available, use
    * {@link PersistenceOperations#newEntity(DirectoryResource, String, String, GenerationType)}
    * 
    * @param project the current project to create the entity. Must not be null
    * @param entityName the name of the entity
    * @param entityPackage the package of the entity to be created
    * @param idStrategy the ID strategy chosen for this entity
    * @return the created java resource
    * @throws FileNotFoundException if something wrong happens while saving the {@link JavaClass}
    */
   public JavaResource newEntity(Project project, String entityName, String entityPackage, GenerationType idStrategy,
            String tableName) throws FileNotFoundException;

   /**
    * Creates a new {@link JavaResource} in the specified target. If a project is available, use
    * {@link PersistenceOperations#newEntity(Project, String, String, GenerationType)}
    * 
    * @param target the target directory resource to create this class
    * @param entityName the name of the entity
    * @param entityPackage the package of the entity to be created
    * @param idStrategy the ID strategy chosen for this entity
    * @param tableName the table name (optional)
    * @return the created java resource
    * @throws FileNotFoundException if something wrong happens while saving the {@link JavaClass}
    */
   public JavaResource newEntity(DirectoryResource target, String entityName, String entityPackage,
            GenerationType idStrategy, String tableName);

   /**
    * Creates a new {@link JavaResource} in the specified project. If no project is available, use
    * {@link PersistenceOperations#newEntity(DirectoryResource, String, String, GenerationType)}
    * 
    * @param project the current project to create the entity. Must not be null
    * @param entityName the name of the entity
    * @param entityPackage the package of the entity to be created
    * @param idStrategy the ID strategy chosen for this entity
    * @return the created java resource
    * @throws FileNotFoundException if something wrong happens while saving the {@link JavaClass}
    */
   public JavaResource newEntity(Project project, String entityName, String entityPackage, GenerationType idStrategy)
            throws FileNotFoundException;

   /**
    * Creates a new {@link JavaResource} in the specified target. If a project is available, use
    * {@link PersistenceOperations#newEntity(Project, String, String, GenerationType)}
    * 
    * @param target the target directory resource to create this class
    * @param entityName the name of the entity
    * @param entityPackage the package of the entity to be created
    * @param idStrategy the ID strategy chosen for this entity
    * @return the created java resource
    * @throws FileNotFoundException if something wrong happens while saving the {@link JavaClass}
    */
   public JavaResource newEntity(DirectoryResource target, String entityName, String entityPackage,
            GenerationType idStrategy);

   /**
    * Creates a new {@link JavaResource} for an embeddable entity in the specified project. If a project is available,
    * use {@link PersistenceOperations#newEmbeddableEntity(Project, String, String)}
    * 
    * @param target the target directory resource to create this class
    * @param entityName the name of the entity
    * @param entityPackage the package of the entity to be created
    * @return the created java resource
    * @throws FileNotFoundException if something wrong happens while saving the {@link JavaClass}
    */
   public JavaResource newEmbeddableEntity(DirectoryResource target, String entityName, String entityPackage);

   /**
    * Creates a new {@link JavaResource} for an embeddable entity in the specified target. If no project is available,
    * use {@link PersistenceOperations#newEmbeddableEntity(DirectoryResource, String, String)}
    * 
    * @param project the current project to create the entity. Must not be null
    * @param entityName the name of the entity
    * @param entityPackage the package of the entity to be created
    * @return the created java resource
    * @throws FileNotFoundException if something wrong happens while saving the {@link JavaClass}
    */
   public JavaResource newEmbeddableEntity(Project project, String entityName, String entityPackage)
            throws FileNotFoundException;

}