/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jpa.api;

import org.apache.maven.model.Repository;
import org.jboss.forge.project.dependencies.Dependency;


public interface MetaModelProvider
{

   /**
    * The dependency containing the APT processor class.
    */
   Dependency getAptDependency();
   
   /**
    * The processor class name.
    */
   String getProcessor();
   
   /**
    * Additional compiler arguments (if any, returns {@code null} otherwise).
    */
   String getCompilerArguments();
   
   /**
    * The dependency does not exist in Maven central, alternative repository.
    * Returns {@code null} if no other repository is needed.
    */
   Repository getAptPluginRepository();

}
