/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.dependency;

/**
 * Used to filter {@link DependencyImpl} objects in collections.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface DependencyFilter
{
   /**
    * Return true if the filter accepts this dependency, or false if the dependency should be filtered out.
    */
   boolean accept(DependencyImpl dependency);
}