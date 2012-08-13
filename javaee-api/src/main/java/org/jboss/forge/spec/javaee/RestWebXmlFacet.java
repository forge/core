/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee;

import org.jboss.forge.project.Facet;

/**
 * @Author Paul Bakker - paul.bakker@luminis.eu
 */
public interface RestWebXmlFacet extends Facet
{
   /**
    * Set the root web-path from which REST requests should be serviced.
    */
   public void setApplicationPath(String path);

   /**
    * Get the path of the JAX-RS servlet
    */
   public String getServletPath();
}
