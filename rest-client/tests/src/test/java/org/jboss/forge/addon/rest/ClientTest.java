/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ClientTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:rest-client"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:rest-client")
               );

      return archive;
   }

   @Inject
   ClientFactory clientFactory;

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
   public void testGetRequest()
   {
      Client client = clientFactory.createClient();
      String response = client.target("http://www.iheartquotes.com/api/v1/random").request(MediaType.TEXT_PLAIN_TYPE)
               .get(String.class);
      Assert.assertNotNull(response);
      Assert.assertFalse(response.isEmpty());

   }
}
