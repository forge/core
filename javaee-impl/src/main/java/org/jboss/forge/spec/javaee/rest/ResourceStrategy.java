package org.jboss.forge.spec.javaee.rest;

public enum ResourceStrategy
{
   /**
    * Expose JPA entities directly in the REST resources.
    */
   JPA_ENTITY,
   
   /**
    * Expose DTOs for JPA entities in the REST resources.
    */
   DTO
}
