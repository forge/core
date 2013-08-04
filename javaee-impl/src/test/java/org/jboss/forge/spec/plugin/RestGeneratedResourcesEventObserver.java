/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.spec.plugin;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.javaee.events.RestGeneratedResources;

/**
 * @author <a href="mailto:salmon.charles@gmail.com">charless</a>
 *
 */
@Singleton
public class RestGeneratedResourcesEventObserver {
	private final List<JavaResource> endpoints = new ArrayList<JavaResource>();
	private final List<JavaResource> entities = new ArrayList<JavaResource>();
	private final List<JavaResource> others = new ArrayList<JavaResource>();

	void reset()
	{
	   endpoints.clear();
	   entities.clear();
	   others.clear();
	}
	
	void generated(@Observes final RestGeneratedResources event)
	   {
			for (JavaResource jr: event.getEndpoints()) {
				this.endpoints.add(jr);
			}
			for (JavaResource jr: event.getEntities()) {
				this.entities.add(jr);
			}
			for (JavaResource jr: event.getOthers()) {
            this.others.add(jr);
         }
	   }

	public List<JavaResource> getEndpoints() {
		return endpoints;
	}

	public List<JavaResource> getEntities() {
		return entities;
	}
	
	public List<JavaResource> getothers() {
      return others;
   }
}
