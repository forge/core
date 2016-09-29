/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.generation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.forge.addon.parser.java.resources.JavaResource;

/**
 * @author <a href="mailto:salmon.charles@gmail.com">charless</a>
 * 
 */
public class RestGeneratedResources
{
   private final List<JavaResource> endpoints;
   private final List<JavaResource> entities;
   private final List<JavaResource> others;

   public RestGeneratedResources()
   {
      this(new ArrayList<JavaResource>(), new ArrayList<JavaResource>(), new ArrayList<JavaResource>());
   }

   public RestGeneratedResources(List<JavaResource> entities,
            List<JavaResource> endpoints)
   {
      this(entities, endpoints, new ArrayList<JavaResource>());
   }

   public RestGeneratedResources(List<JavaResource> entities, List<JavaResource> endpoints, List<JavaResource> others)
   {
      this.entities = entities;
      this.endpoints = endpoints;
      this.others = others;
   }

   public List<JavaResource> getEndpoints()
   {
      return endpoints;
   }

   public List<JavaResource> getEntities()
   {
      return entities;
   }

   public List<JavaResource> getOthers()
   {
      return others;
   }

   public void addToEndpoints(JavaResource endpoint)
   {
      this.endpoints.add(endpoint);
   }

   public void addToEndpoints(Collection<JavaResource> endpoints)
   {
      this.endpoints.addAll(endpoints);
   }

   public void addToEntities(JavaResource entity)
   {
      this.entities.add(entity);
   }

   public void addToEntities(Collection<JavaResource> entities)
   {
      this.entities.addAll(entities);
   }

   public void addToOthers(JavaResource other)
   {
      this.others.add(other);
   }

   public void addToOthers(Collection<JavaResource> others)
   {
      this.others.addAll(others);
   }

}
