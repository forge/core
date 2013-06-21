/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.classloader.sidewaysproxy;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.proxy.ClassLoaderAdapterBuilder;
import org.jboss.forge.proxy.Proxies;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class SidewaysProxyAnonymousCollisionTest
{
   @Deployment(order = 3)
   public static ForgeArchive getDeploymentA()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addClasses(Context.class, ContextImpl.class, ContextValue.class, Action.class, Action1.class,
                        Payload.class, Payload1.class, Extra.class, AbstractExtra.class, ContextValueImpl.class);

      return archive;
   }

   @Deployment(name = "B,1", testable = false, order = 2)
   public static ForgeArchive getDeploymentB()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(Action1.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("D", "1"))
               );

      return archive;
   }

   @Deployment(name = "C,1", testable = false, order = 1)
   public static ForgeArchive getDeploymentC()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(Payload.class, Payload1.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("E", "1"))
               );

      return archive;
   }

   @Deployment(name = "D,1", testable = false, order = 1)
   public static ForgeArchive getDeploymentD()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(Context.class, Action.class, ContextImpl.class, ContextValue.class);

      return archive;
   }

   @Deployment(name = "E,1", testable = false, order = 1)
   public static ForgeArchive getDeploymentE()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(Extra.class, AbstractExtra.class);

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Test
   public void testSidewaysCollision() throws Exception
   {
      ClassLoader A = this.getClass().getClassLoader();
      ClassLoader B = registry.getAddon(AddonId.from("B", "1")).getClassLoader();
      ClassLoader C = registry.getAddon(AddonId.from("C", "1")).getClassLoader();

      Class<?> typeAction1 = B.loadClass(Action1.class.getName());
      Action action1 = getProxiedInstance(A, B, typeAction1);

      Class<?> typePayload1 = C.loadClass(Payload1.class.getName());
      Payload payload1 = getProxiedInstance(A, C, typePayload1);

      Context context = new ContextImpl();
      ContextValue<Payload> value = new ContextValueImpl<Payload>();
      value.set(payload1);
      context.set(value);

      action1.handle(context);
   }

   @SuppressWarnings("unchecked")
   private <T> T getProxiedInstance(ClassLoader A, ClassLoader B, Class<?> type)
            throws InstantiationException,
            IllegalAccessException
   {
      Object delegate = type.newInstance();
      T enhanced = (T) ClassLoaderAdapterBuilder.callingLoader(A).delegateLoader(B).enhance(delegate);
      Assert.assertTrue(Proxies.isForgeProxy(enhanced));
      return enhanced;
   }
}
