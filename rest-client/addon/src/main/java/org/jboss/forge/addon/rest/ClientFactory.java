/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.rest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * Service to create REST clients
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ClientFactory
{
   /**
    * Creates a {@link ClientBuilder}
    */
   public ClientBuilder createClientBuilder()
   {
      return ClientBuilder.newBuilder();
   }

   /**
    * Creates a {@link Client}
    */
   public Client createClient()
   {
      return createClientBuilder().build();
   }

}
