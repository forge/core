/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resources;

/**
 * @author Mike Brock .
 */
public enum ResourceFlag
{
   /**
    * The resource instance is a Prototype (a factory instance). This instance is maintained by the framework to spawn
    * new instances, but does not actually represent a real resource.
    */
   Prototype,

   /**
    * The resource was qualified by an ambiguous qualifier (a wildcard) as opposed to being uniquely qualified.
    */
   AmbiguouslyQualified,

   /**
    * The resource represents a node, which contains or is at least capable of having children..
    */
   Node,

   /**
    * The resource is a leaf, and therefore has no children.
    */
   Leaf,

   /**
    * The resource is a physical file.
    */
   File,

   /**
    * The resource is a project source file.
    */
   ProjectSourceFile,

   /**
    * The resource is a test source file for the current project.
    */
   ProjectTestSourceFile
}
