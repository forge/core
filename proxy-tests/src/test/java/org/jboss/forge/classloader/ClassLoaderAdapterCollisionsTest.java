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
import org.jboss.forge.classloader.mock.collisions.ClassCreatesInstanceFromClassLoader;
import org.jboss.forge.classloader.mock.collisions.ClassImplementsInterfaceExtendsInterfaceValue;
import org.jboss.forge.classloader.mock.collisions.ClassImplementsInterfaceModifiableContext;
import org.jboss.forge.classloader.mock.collisions.ClassImplementsInterfaceWithArrayParameterModification;
import org.jboss.forge.classloader.mock.collisions.ClassImplementsInterfaceWithGetterAndSetter;
import org.jboss.forge.classloader.mock.collisions.ClassImplementsInterfaceWithPassthroughMethod;
import org.jboss.forge.classloader.mock.collisions.ClassWithGetterAndSetter;
import org.jboss.forge.classloader.mock.collisions.ClassWithPassthroughMethod;
import org.jboss.forge.classloader.mock.collisions.InterfaceValue;
import org.jboss.forge.classloader.mock.collisions.InterfaceWithGetterAndSetter;
import org.jboss.forge.classloader.mock.collisions.InterfaceWithPassthroughMethod;
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
public class ClassLoaderAdapterCollisionsTest
{
   @Deployment(order = 3)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addPackages(true, ClassWithGetterAndSetter.class.getPackage())
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("dep", "1")),
                        AddonDependencyEntry.create(AddonId.from("dep", "2"))
               );

      return archive;
   }

   @Deployment(name = "dep,1", testable = false, order = 2)
   public static ForgeArchive getDeploymentDep1()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addPackages(true, ClassWithGetterAndSetter.class.getPackage())
               .addBeansXML();

      return archive;
   }

   @Deployment(name = "dep,2", testable = false, order = 1)
   public static ForgeArchive getDeploymentDep2()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addPackages(true, ClassWithGetterAndSetter.class.getPackage())
               .addBeansXML();

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Test
   public void testSimpleAssignmentCollision() throws Exception
   {
      ClassLoader thisLoader = ClassLoaderAdapterCollisionsTest.class.getClassLoader();
      ClassLoader dep1Loader = registry.getAddon(AddonId.from("dep", "1")).getClassLoader();

      ClassWithGetterAndSetter enhanced;
      try
      {
         enhanced = (ClassWithGetterAndSetter) dep1Loader.loadClass(
                  ClassWithGetterAndSetter.class.getName()).newInstance();
         Assert.fail("Should have received a " + ClassCastException.class.getName());
      }
      catch (ClassCastException e)
      {
      }
      catch (Exception e)
      {
         Assert.fail("Should have received a " + ClassCastException.class.getName());
      }

      Object delegate = dep1Loader.loadClass(ClassWithGetterAndSetter.class.getName()).newInstance();
      enhanced = (ClassWithGetterAndSetter) ClassLoaderAdapterBuilder.callingLoader(thisLoader)
               .delegateLoader(dep1Loader).enhance(delegate);

      Assert.assertFalse(Proxies.isForgeProxy(delegate));
      Assert.assertFalse(Proxies.isProxyType(delegate.getClass()));
      Assert.assertTrue(Proxies.isForgeProxy(enhanced));
      Assert.assertTrue(Proxies.isProxyType(enhanced.getClass()));
      Assert.assertNotNull(enhanced);
      Assert.assertNull(enhanced.getPassthrough());
   }

   @Test
   public void testParameterTypeCollision() throws Exception
   {
      ClassLoader thisLoader = ClassLoaderAdapterCollisionsTest.class.getClassLoader();
      ClassLoader loader1 = registry.getAddon(AddonId.from("dep", "1")).getClassLoader();

      try
      {
         ClassWithGetterAndSetter local = new ClassWithGetterAndSetter();
         local.setPassthrough((ClassWithPassthroughMethod) loader1
                  .loadClass(ClassWithPassthroughMethod.class.getName())
                  .newInstance());

         Assert.fail("Should have received a " + ClassCastException.class.getName());
      }
      catch (ClassCastException e)
      {
      }
      catch (Exception e)
      {
         Assert.fail("Should have received a " + ClassCastException.class.getName());
      }

      Object delegate = loader1.loadClass(ClassWithGetterAndSetter.class.getName()).newInstance();
      ClassWithGetterAndSetter enhanced = (ClassWithGetterAndSetter) ClassLoaderAdapterBuilder
               .callingLoader(thisLoader).delegateLoader(loader1).enhance(delegate);

      enhanced.setPassthrough(new ClassWithPassthroughMethod());

      Assert.assertNotNull(enhanced);
   }

   @Test
   public void testParameterTypeCollisionRoundTrip() throws Exception
   {
      ClassLoader thisLoader = ClassLoaderAdapterCollisionsTest.class.getClassLoader();
      ClassLoader loader1 = registry.getAddon(AddonId.from("dep", "1")).getClassLoader();

      try
      {
         ClassWithGetterAndSetter local = new ClassWithGetterAndSetter();
         local.setPassthrough((ClassWithPassthroughMethod) loader1
                  .loadClass(ClassWithPassthroughMethod.class.getName())
                  .newInstance());

         Assert.fail("Should have received a " + ClassCastException.class.getName());
      }
      catch (ClassCastException e)
      {
      }
      catch (Exception e)
      {
         Assert.fail("Should have received a " + ClassCastException.class.getName());
      }

      Object delegate = loader1.loadClass(ClassWithGetterAndSetter.class.getName()).newInstance();
      ClassWithGetterAndSetter enhanced = (ClassWithGetterAndSetter) ClassLoaderAdapterBuilder
               .callingLoader(thisLoader).delegateLoader(loader1).enhance(delegate);

      ClassWithPassthroughMethod parameterValue = new ClassWithPassthroughMethod();

      enhanced.setPassthrough(parameterValue);
      Assert.assertNotNull(enhanced);

      ClassWithPassthroughMethod result = enhanced.getPassthrough();
      Assert.assertNotNull(result);
   }

   @Test
   public void testInterfaceSimpleAssignmentCollision() throws Exception
   {
      ClassLoader thisLoader = ClassLoaderAdapterCollisionsTest.class.getClassLoader();
      ClassLoader dep1Loader = registry.getAddon(AddonId.from("dep", "1")).getClassLoader();

      InterfaceWithGetterAndSetter enhanced;
      try
      {
         enhanced = (InterfaceWithGetterAndSetter) dep1Loader.loadClass(
                  ClassImplementsInterfaceWithGetterAndSetter.class.getName()).newInstance();
         Assert.fail("Should have received a " + ClassCastException.class.getName());
      }
      catch (ClassCastException e)
      {
      }
      catch (Exception e)
      {
         Assert.fail("Should have received a " + ClassCastException.class.getName());
      }

      Object delegate = dep1Loader.loadClass(ClassImplementsInterfaceWithGetterAndSetter.class.getName())
               .newInstance();
      enhanced = (InterfaceWithGetterAndSetter) ClassLoaderAdapterBuilder.callingLoader(thisLoader)
               .delegateLoader(dep1Loader).enhance(delegate);

      Assert.assertFalse(Proxies.isForgeProxy(delegate));
      Assert.assertFalse(Proxies.isProxyType(delegate.getClass()));
      Assert.assertTrue(Proxies.isForgeProxy(enhanced));
      Assert.assertTrue(Proxies.isProxyType(enhanced.getClass()));
      Assert.assertNotNull(enhanced);
      Assert.assertNull(enhanced.getPassthrough());
   }

   @Test
   public void testInterfaceParameterTypeCollision() throws Exception
   {
      ClassLoader thisLoader = ClassLoaderAdapterCollisionsTest.class.getClassLoader();
      ClassLoader loader1 = registry.getAddon(AddonId.from("dep", "1")).getClassLoader();

      try
      {
         InterfaceWithGetterAndSetter local = new ClassImplementsInterfaceWithGetterAndSetter();
         local.setPassthrough((InterfaceWithPassthroughMethod) loader1
                  .loadClass(ClassImplementsInterfaceWithPassthroughMethod.class.getName())
                  .newInstance());

         Assert.fail("Should have received a " + ClassCastException.class.getName());
      }
      catch (ClassCastException e)
      {
      }
      catch (Exception e)
      {
         Assert.fail("Should have received a " + ClassCastException.class.getName());
      }

      Object delegate = loader1.loadClass(ClassImplementsInterfaceWithGetterAndSetter.class.getName()).newInstance();
      InterfaceWithGetterAndSetter enhanced = (InterfaceWithGetterAndSetter) ClassLoaderAdapterBuilder
               .callingLoader(thisLoader).delegateLoader(loader1).enhance(delegate);

      enhanced.setPassthrough(new ClassImplementsInterfaceWithPassthroughMethod());

      Assert.assertNotNull(enhanced);
   }

   @Test
   public void testInterfaceParameterTypeCollisionRoundTrip() throws Exception
   {
      ClassLoader thisLoader = ClassLoaderAdapterCollisionsTest.class.getClassLoader();
      ClassLoader loader1 = registry.getAddon(AddonId.from("dep", "1")).getClassLoader();

      try
      {
         InterfaceWithGetterAndSetter local = new ClassImplementsInterfaceWithGetterAndSetter();
         local.setPassthrough((InterfaceWithPassthroughMethod) loader1
                  .loadClass(ClassImplementsInterfaceWithPassthroughMethod.class.getName())
                  .newInstance());

         Assert.fail("Should have received a " + ClassCastException.class.getName());
      }
      catch (ClassCastException e)
      {
      }
      catch (Exception e)
      {
         Assert.fail("Should have received a " + ClassCastException.class.getName());
      }

      Object delegate = loader1.loadClass(ClassImplementsInterfaceWithGetterAndSetter.class.getName()).newInstance();
      InterfaceWithGetterAndSetter enhanced = (InterfaceWithGetterAndSetter) ClassLoaderAdapterBuilder
               .callingLoader(thisLoader).delegateLoader(loader1).enhance(delegate);

      enhanced.setPassthrough(new ClassImplementsInterfaceWithPassthroughMethod());
      Assert.assertNotNull(enhanced);

      InterfaceWithPassthroughMethod result = enhanced.getPassthrough();
      Assert.assertNotNull(result);
   }

   /*
    * Three Classloaders
    */

   @Test
   public void testReturnTypeEnhancementFromThirdPartyLoader() throws Exception
   {
      ClassLoader thisLoader = ClassLoaderAdapterCollisionsTest.class.getClassLoader();
      ClassLoader dep1Loader = registry.getAddon(AddonId.from("dep", "1")).getClassLoader();
      ClassLoader dep2Loader = registry.getAddon(AddonId.from("dep", "2")).getClassLoader();

      ClassCreatesInstanceFromClassLoader creator;
      try
      {
         @SuppressWarnings("unused")
         ClassWithGetterAndSetter result = new ClassCreatesInstanceFromClassLoader()
                  .create(dep2Loader, ClassWithGetterAndSetter.class);
         Assert.fail("Should have received a " + ClassCastException.class.getName());
      }
      catch (ClassCastException e)
      {
      }
      catch (Exception e)
      {
         Assert.fail("Should have received a " + ClassCastException.class.getName());
      }

      Object delegate = dep1Loader.loadClass(ClassCreatesInstanceFromClassLoader.class.getName()).newInstance();
      creator = (ClassCreatesInstanceFromClassLoader) ClassLoaderAdapterBuilder.callingLoader(thisLoader)
               .delegateLoader(dep1Loader).enhance(delegate);

      ClassWithGetterAndSetter create = creator.create(dep2Loader, ClassWithGetterAndSetter.class);
      Assert.assertNotNull(create);
   }

   @Test
   public void testReturnTypeNativeAccessAfterParameterTypeEnhancementFromEnhancedClass() throws Exception
   {
      ClassLoader thisLoader = ClassLoaderAdapterCollisionsTest.class.getClassLoader();
      ClassLoader dep1Loader = registry.getAddon(AddonId.from("dep", "1")).getClassLoader();
      ClassLoader dep2Loader = registry.getAddon(AddonId.from("dep", "2")).getClassLoader();

      ClassImplementsInterfaceWithArrayParameterModification modifier = (ClassImplementsInterfaceWithArrayParameterModification) ClassLoaderAdapterBuilder
               .callingLoader(thisLoader)
               .delegateLoader(dep1Loader)
               .enhance(dep1Loader.loadClass(ClassImplementsInterfaceWithArrayParameterModification.class.getName())
                        .newInstance());

      modifier.setValueClassLoader(dep2Loader);

      List<InterfaceValue> values = new ArrayList<InterfaceValue>();
      modifier.modifyParameter(new ClassImplementsInterfaceModifiableContext(values));
      InterfaceValue result = values.get(0);
      ClassImplementsInterfaceExtendsInterfaceValue value = (ClassImplementsInterfaceExtendsInterfaceValue) result;
      Assert.assertNotNull(value);
   }
}
