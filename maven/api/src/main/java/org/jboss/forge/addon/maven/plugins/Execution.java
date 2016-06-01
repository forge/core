/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.plugins;

import java.util.List;

/**
 * Represents an <execution> element in the pom.xml
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface Execution
{
   /**
    * @return the ID of this execution
    */
   String getId();

   /**
    * @return the phase where this execution belongs
    */
   String getPhase();

   /**
    * @return the goals configured for this execution
    */
   List<String> getGoals();

   /**
    * @return the {@link Configuration} object associated with this execution
    */
   Configuration getConfig();
}
