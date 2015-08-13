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
