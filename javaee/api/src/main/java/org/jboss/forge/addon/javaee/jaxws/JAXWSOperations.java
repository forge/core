/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jaxws;

import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * This class contains JAX-WS specific operations
 *
 * @author <a href="mailto:antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public interface JAXWSOperations {

    /**
     * Creates a new JAX-WS Web Service
     *
     * @param source the current source to decorate
     * @return the decorated {@link JavaClassSource}
     */
    JavaClassSource newWebService(JavaClassSource source);
}
