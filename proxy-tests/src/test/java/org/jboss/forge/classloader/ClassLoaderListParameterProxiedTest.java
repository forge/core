/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.classloader;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.classloader.mock.MockResult;
import org.jboss.forge.classloader.mock.Result;
import org.jboss.forge.classloader.mock.collisions.ClassWithListAsParameter;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.proxy.ClassLoaderAdapterBuilder;
import org.jboss.forge.proxy.ClassLoaderAdapterBuilderDelegateLoader;
import org.jboss.forge.proxy.Proxies;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ClassLoaderListParameterProxiedTest
{
   @Deployment(order = 3)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addClasses(Result.class, MockResult.class, ClassWithListAsParameter.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("dep", "1"))
               );

      return archive;
   }

   @Deployment(name = "dep,1", testable = false, order = 2)
   public static ForgeArchive getDeploymentDep1()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(Result.class, MockResult.class, ClassWithListAsParameter.class)
               .addBeansXML();

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Test
   public void test() throws Exception
   {
      ClassLoader thisLoader = ClassLoaderListParameterProxiedTest.class.getClassLoader();
      ClassLoader dep1Loader = registry.getAddon(AddonId.from("dep", "1")).getClassLoader();

      Class<?> foreignType = dep1Loader.loadClass(MockResult.class.getName());
      Object delegateResult = foreignType.newInstance();
      MockResult enhancedResult = (MockResult) ClassLoaderAdapterBuilder.callingLoader(thisLoader)
               .delegateLoader(dep1Loader).enhance(delegateResult);
      Assert.assertTrue(Proxies.isForgeProxy(enhancedResult));

      Object foreignInstance = dep1Loader
               .loadClass(ClassWithListAsParameter.class.getName())
               .newInstance();

      ClassLoaderAdapterBuilderDelegateLoader builder = ClassLoaderAdapterBuilder
               .callingLoader(thisLoader)
               .delegateLoader(dep1Loader);

      Object enhancedFilter = builder.enhance(foreignInstance);

      ClassWithListAsParameter classFilter = (ClassWithListAsParameter) enhancedFilter;

      Assert.assertTrue(Proxies.isForgeProxy(classFilter));

      List<Object> list = new ArrayList<Object>();
      list.add(enhancedResult);
      list.add(delegateResult);
      Assert.assertTrue(classFilter.verify(list));

   }
}
