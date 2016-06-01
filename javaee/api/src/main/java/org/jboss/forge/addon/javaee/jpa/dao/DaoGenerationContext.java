/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.dao;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * Parameters for DAO resource generation.
 * 
 * @author <a href="salem.elrahal@gmail.com">Salem Elrahal</a>
 *
 */
public class DaoGenerationContext
{
   private Project project;
   private JavaClassSource entity;
   private String targetPackageName;
   private String persistenceUnitName;

   public JavaClassSource getEntity()
   {
      return entity;
   }

   public void setEntity(JavaClassSource entity)
   {
      this.entity = entity;
   }

   public String getTargetPackageName()
   {
      return targetPackageName;
   }

   public void setTargetPackageName(String targetPackageName)
   {
      this.targetPackageName = targetPackageName;
   }

   public String getPersistenceUnitName()
   {
      return persistenceUnitName;
   }

   public void setPersistenceUnitName(String persistenceUnitName)
   {
      this.persistenceUnitName = persistenceUnitName;
   }

   public Project getProject()
   {
      return project;
   }

   public void setProject(Project project)
   {
      this.project = project;
   }
}
