/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.generation;

import java.util.List;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.text.Inflector;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * Parameters for REST resource generation
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RestGenerationContext
{
   private Project project;
   private JavaClassSource entity;
   private String targetPackageName;
   private List<String> contentType;
   private String persistenceUnitName;
   private Inflector inflector;

   /**
    * @return the project
    */
   public Project getProject()
   {
      return project;
   }

   /**
    * @return the entity
    */
   public JavaClassSource getEntity()
   {
      return entity;
   }

   /**
    * @return the targetPackageName
    */
   public String getTargetPackageName()
   {
      return targetPackageName;
   }

   /**
    * @return the contentType
    */
   public List<String> getContentType()
   {
      return contentType;
   }

   /**
    * @return the persistenceUnitName
    */
   public String getPersistenceUnitName()
   {
      return persistenceUnitName;
   }

   public Inflector getInflector()
   {
      return inflector;
   }

   public void setProject(Project project)
   {
      this.project = project;
   }

   public void setEntity(JavaClassSource entity)
   {
      this.entity = entity;
   }

   public void setTargetPackageName(String targetPackageName)
   {
      this.targetPackageName = targetPackageName;
   }

   public void setContentType(List<String> contentType)
   {
      if (contentType.size() < 1)
      {
         throw new IllegalArgumentException("At least one content type must be specified.");
      }
      this.contentType = contentType;
   }

   public void setPersistenceUnitName(String persistenceUnitName)
   {
      this.persistenceUnitName = persistenceUnitName;
   }

   public void setInflector(Inflector inflector)
   {
      this.inflector = inflector;
   }
}