/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import java.io.FileNotFoundException;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;

/**
 * Interface for JPA-oriented field operations.
 */
public interface JPAFieldOperations
{
   void newOneToOneRelationship(Project project, JavaResource resource, String fieldName,
            String fieldType,
            String inverseFieldName,
            FetchType fetchType, boolean required,
            Iterable<CascadeType> cascadeTypes) throws FileNotFoundException;

   void newManyToOneRelationship(
            Project project,
            JavaResource resource,
            String fieldName,
            String fieldType,
            String inverseFieldName,
            FetchType fetchType,
            boolean required,
            Iterable<CascadeType> cascadeTypes) throws FileNotFoundException;

   void newOneToManyRelationship(
            Project project,
            JavaResource resource,
            String fieldName,
            String fieldType,
            String inverseFieldName,
            FetchType fetchType,
            Iterable<CascadeType> cascadeTypes)
            throws FileNotFoundException;

   void newManyToManyRelationship(
            Project project,
            JavaResource resource,
            String fieldName,
            String fieldType,
            String inverseFieldName,
            FetchType fetchType,
            Iterable<CascadeType> cascadeTypes) throws FileNotFoundException;

   void newEmbeddedRelationship(
            Project project,
            JavaResource resource,
            String fieldName,
            String fieldType)
            throws FileNotFoundException;
}
