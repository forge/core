/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.spec.javaee.events;

import java.util.List;

import org.jboss.forge.resources.java.JavaResource;

/**
 * @author <a href="mailto:salmon.charles@gmail.com">charless</a>
 * 
 */
public class RestGeneratedResources {
	private final List<JavaResource> endpoints;
	private final List<JavaResource> entities;
	private final List<JavaResource> dtos;

	public RestGeneratedResources(List<JavaResource> entities,
			List<JavaResource> endpoints, List<JavaResource> dtos) {
		this.entities = entities;
		this.endpoints = endpoints;
		this.dtos = dtos;
	}

	public List<JavaResource> getEndpoints() {
		return endpoints;
	}

	public List<JavaResource> getEntities() {
		return entities;
	}
	
	public List<JavaResource> getDtos() {
      return dtos;
   }

}
