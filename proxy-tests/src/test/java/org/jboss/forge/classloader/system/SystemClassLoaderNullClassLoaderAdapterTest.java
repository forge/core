/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.classloader.system;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.proxy.ClassLoaderAdapterBuilder;
import org.jboss.forge.proxy.Proxies;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
/**
 * FORGE-928: java.util.ArrayList.class.getClassLoader() returns null
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class SystemClassLoaderNullClassLoaderAdapterTest
{

   @Deployment(order = 3)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addClasses(ArrayListFactory.class,
                        EmptyClassLoader.class);

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Inject
   private Furnace furnace;

   @SuppressWarnings("unchecked")
   @Test
   public void testNullSystemClassLoaderDefaultsToFurnaceProxyCL() throws Exception
   {
      ClassLoader thisLoader = SystemClassLoaderNullClassLoaderAdapterTest.class.getClassLoader();

      ClassLoader dep1Loader = new EmptyClassLoader();
      Class<?> foreignType = dep1Loader.loadClass(ArrayListFactory.class.getName());
      Object foreignInstance = foreignType.newInstance();
      List<?> proxy = (List<?>) foreignType.getMethod("getArrayList").invoke(foreignInstance);
      Assert.assertFalse(Proxies.isForgeProxy(proxy));

      Object delegate = foreignType.newInstance();
      ArrayListFactory enhancedFactory = (ArrayListFactory) ClassLoaderAdapterBuilder.callingLoader(thisLoader)
               .delegateLoader(dep1Loader).enhance(delegate);

      Assert.assertTrue(Proxies.isForgeProxy(enhancedFactory));

      @SuppressWarnings("rawtypes")
      List enhancedInstance = enhancedFactory.getArrayList();
      enhancedInstance.add(new ArrayListFactory());
      enhancedInstance.get(0);
      Assert.assertTrue(Proxies.isForgeProxy(enhancedInstance));

   }
}
