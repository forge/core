/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies;

import org.jboss.forge.project.Project;

/**
 * Responsible for installing a given {@link Dependency} into the current project. Resolves available dependencies.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface DependencyInstaller
{
   /**
    * Install given {@link Dependency} with the default {@link ScopeType}. This method overwrites existing dependencies.
    * Any {@link Dependency#getPackagingType()}, {@link Dependency#getClassifier()} , and
    * {@link Dependency#getScopeType()} will be preserved in the added managed dependency.
    */
   Dependency install(Project project, Dependency dependency);

   /**
    * Install given {@link Dependency} with the default {@link ScopeType}, applying the given {@link DependencyFilter}.
    * This method overwrites existing dependencies. Any {@link Dependency#getPackagingType()},
    * {@link Dependency#getClassifier()}, and {@link Dependency#getScopeType()} will be preserved in the added managed
    * dependency.
    */
   Dependency install(Project project, Dependency dependency,
            DependencyFilter filter);

   /**
    * Install given {@link Dependency} with the given {@link ScopeType}. This method overwrites existing dependencies.
    * Any {@link Dependency#getPackagingType()}, {@link Dependency#getClassifier()} , and
    * {@link Dependency#getScopeType()} will be preserved in the added managed dependency.
    */
   Dependency install(Project project, Dependency dependency, ScopeType type);

   /**
    * Install given {@link Dependency} with the given {@link ScopeType} and {@link DependencyFilter}. This method
    * overwrites existing dependencies. Any {@link Dependency#getPackagingType()}, {@link Dependency#getClassifier()} ,
    * and {@link Dependency#getScopeType()} will be preserved in the added managed dependency.
    */
   Dependency install(Project project, Dependency dependency, ScopeType type,
            DependencyFilter filter);

   /**
    * Install given managed {@link Dependency} with the default {@link ScopeType}. This method overwrites existing
    * managed dependencies. Any {@link Dependency#getPackagingType()}, {@link Dependency#getClassifier()}, and
    * {@link Dependency#getScopeType()} will be preserved.
    */
   Dependency installManaged(Project project, Dependency dependency);

   /**
    * Install given managed {@link Dependency} with the default {@link ScopeType}, applying the given
    * {@link DependencyFilter}. This method overwrites existing managed dependencies. Any
    * {@link Dependency#getPackagingType()}, {@link Dependency#getClassifier()} , and {@link Dependency#getScopeType()}
    * will be preserved.
    */
   Dependency installManaged(Project project, Dependency dependency,
            DependencyFilter filter);

   /**
    * Install given managed {@link Dependency} with the given {@link ScopeType} . This method overwrites existing
    * dependencies. Any {@link Dependency#getPackagingType()}, {@link Dependency#getClassifier()} , and
    * {@link Dependency#getScopeType()} will be preserved.
    */
   Dependency installManaged(Project project, Dependency dependency,
            ScopeType type);

   /**
    * Install given managed {@link Dependency} with the given {@link ScopeType} and {@link DependencyFilter}. This
    * method overwrites existing dependencies. Any {@link Dependency#getPackagingType()},
    * {@link Dependency#getClassifier()} , and {@link Dependency#getScopeType()} will be preserved.
    */
   Dependency installManaged(Project project, Dependency dependency,
            ScopeType type, DependencyFilter filter);

   /**
    * Returns whether or not the given {@link Dependency} is installed.
    */
   boolean isInstalled(Project project, Dependency dependency);
}
