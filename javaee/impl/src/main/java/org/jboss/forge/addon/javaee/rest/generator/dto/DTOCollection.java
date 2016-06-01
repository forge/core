/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.generator.dto;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * A class that represents a collection of DTOs created for JPA entities. This is used to ensure that DTO creation is
 * less expensive, since the JPA entities may be referenced several times in the object graph.
 *
 */
public class DTOCollection
{

   private final Map<JavaClass<?>, DTOPair> dtos = new HashMap<>();

   /**
    * Retrieves all the DTOs present in this instance.
    *
    * @return A {@link Collection} of all the DTOs present in this {@link DTOCollection} instance.
    */
   public Collection<JavaClassSource> allResources()
   {
      Set<JavaClassSource> result = new HashSet<>();
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
   public void addRootDTO(JavaClass<?> entity, JavaClassSource rootDTO)
   {
      DTOPair dtoPair = dtos.containsKey(entity) ? dtos.get(entity) : new DTOPair();
      dtoPair.rootDTO = rootDTO;
      dtos.put(entity, dtoPair);
   }

   /**
    * Registers the nested DTO created for a JPA entity
    *
    * @param entity The JPA entity
    * @param nestedDTO The nested DTO created for the JPA entity
    */
   public void addNestedDTO(JavaClass<?> entity, JavaClassSource nestedDTO)
   {
      DTOPair dtoPair = dtos.containsKey(entity) ? dtos.get(entity) : new DTOPair();
      dtoPair.nestedDTO = nestedDTO;
      dtos.put(entity, dtoPair);
   }

   /**
    * Indicates whether a DTO is found in the underlying collection or not.
    *
    * @param entity The JPA entity for which DTOs may have been created
    * @param root Toplevel/Root or nested DTO?
    * @return <code>true</code> if a DTO at the desired level (root/nested) for the provided entity was found in the
    *         collection
    */
   public boolean containsDTOFor(JavaClass<?> entity, boolean root)
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
   public JavaClassSource getDTOFor(JavaClass<?> entity, boolean root)
   {
      if (dtos.get(entity) == null)
      {
         return null;
      }
      return root ? (dtos.get(entity).rootDTO) : (dtos.get(entity).nestedDTO);
   }

   /**
    * A pair of root and nested DTOs, linked to a JPA entity instance in a {@link DTOCollection}.
    */
   private static class DTOPair
   {
      JavaClassSource rootDTO;
      JavaClassSource nestedDTO;
   }

}
