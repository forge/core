/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee;

/**
 * Constants used for package names in a Java EE project.
 * 
 * @author <a href="mailto:antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public interface JavaEEPackageConstants
{
   String DEFAULT_SERVICE_PACKAGE = "service";
   // JSF
   String DEFAULT_FACES_PACKAGE = "view";
   String DEFAULT_FACES_CONVERTER_PACKAGE = DEFAULT_FACES_PACKAGE + "." + "converter";
   String DEFAULT_FACES_VALIDATOR_PACKAGE = DEFAULT_FACES_PACKAGE + "." + "validator";
   // JPA
   String DEFAULT_ENTITY_PACKAGE = "model";
   // Bean Validation
   String DEFAULT_CONSTRAINT_PACKAGE = "constraints";
   // Servlet
   String DEFAULT_SERVLET_PACKAGE = "web";
   // CDI
   String DEFAULT_CDI_PACKAGE = "beans";
   String DEFAULT_CDI_EXTENSIONS_PACKAGE = DEFAULT_CDI_PACKAGE + "." + "extensions";
   // REST
   String DEFAULT_REST_PACKAGE = "rest";
   // SOAP
   String DEFAULT_SOAP_PACKAGE = "soap";
   // WebSocket
   String DEFAULT_WEBSOCKET_PACKAGE = "ws";
}
