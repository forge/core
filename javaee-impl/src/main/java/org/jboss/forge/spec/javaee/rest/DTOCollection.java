/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.rest;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.project.ProjectScoped;

/**
 * A class that represents a collection of DTOs created for JPA entities. This is used to ensure that DTO creation is
 * less expensive, since the JPA entities may be referenced several times in the object graph.
 * 
 */
public class DTOCollection
{

   private Map<JavaClass, DTOPair> dtos = new HashMap<JavaClass, DTOPair>();

   @Inject
   @ProjectScoped
   private Project project;

   @Inject
   private ShellPrompt prompt;

   /**
    * Retrieves all the DTOs present in this instance.
    * 
    * @return A {@link Collection} of all the DTOs present in this {@link DTOCollection} instance.
    */
   public Collection<JavaResource> allResources()
   {
      List<JavaResource> result = new ArrayList<JavaResource>();
      for (DTOPair pair : dtos.values())
      {
         if (pair.rootDTO != null)
         {
            result.add(pair.rootDTO);
         }
         if (pair.nestedDTO != null)
         {
            result.add(pair.nestedDTO);
         }
      }
      return result;
   }

   /**
    * Registers the root DTO created for a JPA entity
    * 
    * @param entity The JPA entity
    * @param rootDTO The root DTO created for the JPA entity
    */
   public void addRootDTO(JavaClass entity, JavaClass rootDTO)
   {
      JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
      DTOPair dtoPair = dtos.containsKey(entity) ? dtos.get(entity) : new DTOPair();
      if (rootDTO != null)
      {
         try
         {
            if (!java.getJavaResource(rootDTO).exists()
                     || prompt.promptBoolean("DTO [" + rootDTO.getQualifiedName() + "] already, exists. Overwrite?"))
            {
               dtoPair.rootDTO = java.saveJavaSource(rootDTO);
            }
         }
         catch (FileNotFoundException fileEx)
         {
            throw new RuntimeException(fileEx);
         }
      }
      dtos.put(entity, dtoPair);
   }

   /**
    * Registers the nested DTO created for a JPA entity
    * 
    * @param entity The JPA entity
    * @param nestedDTO The nested DTO created for the JPA entity
    */
   public void addNestedDTO(JavaClass entity, JavaClass nestedDTO)
   {
      JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
      DTOPair dtoPair = dtos.containsKey(entity) ? dtos.get(entity) : new DTOPair();
      if (nestedDTO != null)
      {
         try
         {
            if (!java.getJavaResource(nestedDTO).exists()
                     || prompt.promptBoolean("DTO [" + nestedDTO.getQualifiedName() + "] already, exists. Overwrite?"))
            {
               dtoPair.nestedDTO = java.saveJavaSource(nestedDTO);
            }
         }
         catch (FileNotFoundException fileEx)
         {
            throw new RuntimeException(fileEx);
         }
      }
      dtos.put(entity, dtoPair);
   }

   /**
    * A pair of root and nested DTOs, linked to a JPA entity instance in a {@link DTOCollection}.
    */
   public class DTOPair
   {
      private JavaResource rootDTO;
      private JavaResource nestedDTO;

      /**
       * Returns the Root DTO
       * 
       * @return the root DTO in the pair
       */
      public JavaResource getRootDTO()
      {
         return rootDTO;
      }

      /**
       * Returns the Nested DTO
       * 
       * @return the nested DTO in the pair
       */
      public JavaResource getNestedDTO()
      {
         return nestedDTO;
      }
   }

   /**
    * Indicates whether a DTO is found in the underlying collection or not.
    * 
    * @param entity The JPA entity for which DTOs may have been created
    * @param root Toplevel/Root or nested DTO?
    * @return <code>true</code> if a DTO at the desired level (root/nested) for the provided entity was found in the
    *         collection
    */
   public boolean containsDTOFor(JavaClass entity, boolean root)
   {
      if (dtos.get(entity) == null)
      {
         return false;
      }
      return (root ? (dtos.get(entity).rootDTO != null) : (dtos.get(entity).nestedDTO != null));
   }

   /**
    * Retrieves the DTO created for the JPA entity, depending on whether a top level or nested DTO was requested as the
    * return value.
    * 
    * @param entity The JPA entity for which the DTOs may have been created
    * @param root True, if the root/toplevel DTO should be returned. False if nested DTOs are to be returned.
    * @return The root or nested DTO created for the JPA entity. <code>null</code> if no DTO was found.
    */
   public JavaClass getDTOFor(JavaClass entity, boolean root)
   {
      if (dtos.get(entity) == null)
      {
         return null;
      }
      JavaResource javaResource = root ? (dtos.get(entity).rootDTO) : (dtos.get(entity).nestedDTO);
      JavaClass javaClass;
      try
      {
         javaClass = (JavaClass) javaResource.getJavaSource();
      }
      catch (FileNotFoundException fileEx)
      {
         return null;
      }
      return javaClass;
   }
}
