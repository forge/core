/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ClientTest
{
   ClientFactory clientFactory;

   @Before
   public void setUp()
   {
      clientFactory = SimpleContainer.getServices(getClass().getClassLoader(), ClientFactory.class).get();
   }

   @Test
   public void testClientImplementationAvailable() throws Exception
   {
      Assert.assertNotNull(clientFactory);
      Assert.assertNotNull(clientFactory.createClientBuilder());
      Client client = clientFactory.createClient();
      Assert.assertNotNull(client);
      Assert.assertThat(client, is(not(clientFactory.createClient())));
   }

   @Test
   @Ignore("Use a local HTTP server")
   public void testGetRequest()
   {
      Client client = clientFactory.createClient();
      String response = client.target("http://www.iheartquotes.com/api/v1/random").request(MediaType.TEXT_PLAIN_TYPE)
               .get(String.class);
      Assert.assertNotNull(response);
      Assert.assertFalse(response.isEmpty());

   }
}
