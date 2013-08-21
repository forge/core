/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.rest;

/**
 * The strategy to use, to create the JAX-RS resources.
 * 
 */
public enum ResourceStrategy
{
   /**
    * Expose JPA entities directly in the REST resources.
    */
   JPA_ENTITY,

   /**
    * Expose DTOs for JPA entities in the REST resources.
    */
   ROOT_AND_NESTED_DTO
}
