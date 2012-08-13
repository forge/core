/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.resources;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.project.resources.ResourceProducer;
import org.jboss.forge.shell.project.resources.ResourceProducerExtension;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ResourceProducerExtensionTest
         extends AbstractShellTest
{

   @Test
   public void testNothing()
   {
   }

   @Deployment
   public static JavaArchive getDeployment()
   {
      return AbstractShellTest.getDeployment()
               .addAsManifestResource("META-INF/services/javax.enterprise.inject.spi.Extension")
               .addClass(ResourceProducerExtension.class)
               .addClass(ResourceProducer.class)
               .addClass(MockResourceInjectionPlugin.class);
   }

   @Inject
   private Event<MockEvent> event;

   @Inject
   @Alias("inject")
   private MockResourceInjectionPlugin plugin;

   @Test
   public void testGenericResourceInjection() throws Exception
   {
      Resource<?> resource = plugin.getR();
      assertNotNull(resource);
   }

   @Test
   public void testSpecificResourceInjection() throws Exception
   {
      DirectoryResource resource = plugin.getD();
      assertNotNull(resource);
   }

   @Test
   public void testSpecificResourceInjectionNullIfIncorrectType() throws Exception
   {
      JavaResource resource = plugin.getJ();
      assertNull(resource);
   }

   @Test
   public void testMethodParameterInjection() throws Exception
   {
      event.fire(new MockEvent());
      Resource<?> resource = plugin.getObservedResource();
      assertNotNull(resource);
   }
}
