/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.DependencyRepository;

/**
 * A {@link MetaModelProvider} is bound to a {@link PersistenceProvider}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface MetaModelProvider
{
   /**
    * The coordinate containing the APT processor class.
    */
   Coordinate getAptCoordinate();

   /**
    * The processor class name.
    */
   String getProcessor();

   /**
    * Additional compiler arguments (if any, returns {@code null} otherwise).
    */
   String getCompilerArguments();

   /**
    * The dependency does not exist in Maven central, alternative repository. Returns {@code null} if no other
    * repository is needed.
    */
   DependencyRepository getAptPluginRepository();

}
