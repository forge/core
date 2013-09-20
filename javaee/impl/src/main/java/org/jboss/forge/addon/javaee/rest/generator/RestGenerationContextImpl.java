/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.rest.generator;

import org.jboss.forge.addon.javaee.rest.generation.RestGenerationContext;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.parser.java.JavaClass;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RestGenerationContextImpl implements RestGenerationContext
{
   private Project project;
   private JavaClass entity;
   private String targetPackageName;
   private String contentType;
   private String persistenceUnitName;

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
   public JavaClass getEntity()
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
   public String getContentType()
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

   public void setProject(Project project)
   {
      this.project = project;
   }

   public void setEntity(JavaClass entity)
   {
      this.entity = entity;
   }

   public void setTargetPackageName(String targetPackageName)
   {
      this.targetPackageName = targetPackageName;
   }

   public void setContentType(String contentType)
   {
      this.contentType = contentType;
   }

   public void setPersistenceUnitName(String persistenceUnitName)
   {
      this.persistenceUnitName = persistenceUnitName;
   }
}