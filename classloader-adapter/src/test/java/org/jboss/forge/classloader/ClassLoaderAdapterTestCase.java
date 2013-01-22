package org.jboss.forge.classloader;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;

import org.junit.Assert;
import org.junit.Test;

public class ClassLoaderAdapterTestCase
{
   @Test
   public void testAdaptProxiedType() throws Exception
   {
      ClassLoader loader = MockService.class.getClassLoader();
      final MockService internal = new MockService();

      MockService delegate = (MockService) Enhancer.create(MockService.class, new LazyLoader()
      {
         @Override
         public Object loadObject() throws Exception
         {
            return internal;
         }
      });

      MockService adapter = ClassLoaderAdapterCallback.enhance(loader, loader, delegate, MockService.class);

      Assert.assertNotEquals(internal.getResult(), adapter.getResult());
      Assert.assertEquals(internal.getResult().getValue(), adapter.getResult().getValue());
   }

   @Test
   public void testAdaptProxiedResult() throws Exception
   {
      ClassLoader loader = MockService.class.getClassLoader();
      final MockService internal = new MockService();

      MockService delegate = (MockService) Enhancer.create(MockService.class, new LazyLoader()
      {
         @Override
         public Object loadObject() throws Exception
         {
            return internal;
         }
      });

      MockService adapter = ClassLoaderAdapterCallback.enhance(loader, loader, delegate, MockService.class);

      Assert.assertNotEquals(internal.getResult(), adapter.getResultEnhanced());
   }

   @Test
   public void testAdaptProxiedResultReturnTypeObject() throws Exception
   {
      ClassLoader loader = MockService.class.getClassLoader();
      final MockService internal = new MockService();

      MockService delegate = (MockService) Enhancer.create(MockService.class, new LazyLoader()
      {
         @Override
         public Object loadObject() throws Exception
         {
            return internal;
         }
      });

      MockService adapter = ClassLoaderAdapterCallback.enhance(loader, loader, delegate, MockService.class);

      Assert.assertNotEquals(internal.getResult(), adapter.getResultEnhancedReturnTypeObject());
   }

   @Test(expected = ClassCastException.class)
   public void testCannotAdaptFinalResultReturnType() throws Exception
   {
      ClassLoader loader = MockService.class.getClassLoader();
      final MockService internal = new MockService();

      MockService delegate = (MockService) Enhancer.create(MockService.class, new LazyLoader()
      {
         @Override
         public Object loadObject() throws Exception
         {
            return internal;
         }
      });

      MockService adapter = ClassLoaderAdapterCallback.enhance(loader, loader, delegate, MockService.class);
      adapter.getResultFinalReturnType();
   }

   @Test
   public void testCanAdaptFinalResultReturnTypeObject() throws Exception
   {
      ClassLoader loader = MockService.class.getClassLoader();
      final MockService internal = new MockService();

      MockService delegate = (MockService) Enhancer.create(MockService.class, new LazyLoader()
      {
         @Override
         public Object loadObject() throws Exception
         {
            return internal;
         }
      });

      MockService adapter = ClassLoaderAdapterCallback.enhance(loader, loader, delegate, MockService.class);

      Assert.assertNotEquals(internal.getResultFinalReturnTypeObject(), adapter.getResultFinalReturnTypeObject());
      Assert.assertEquals(((Result) internal.getResultFinalReturnTypeObject()).getValue(),
               ((Result) adapter.getResultFinalReturnTypeObject()).getValue());
   }

   @Test
   public void testAdaptFinalResult() throws Exception
   {
      ClassLoader loader = MockService.class.getClassLoader();
      final MockService internal = new MockService();

      MockService delegate = (MockService) Enhancer.create(MockService.class, new LazyLoader()
      {
         @Override
         public Object loadObject() throws Exception
         {
            return internal;
         }
      });

      MockService adapter = ClassLoaderAdapterCallback.enhance(loader, loader, delegate, MockService.class);

      Assert.assertNotEquals(internal.getResultInterfaceFinalImpl(), adapter.getResultInterfaceFinalImpl());
   }

   @Test
   public void testNullValuesAsMethodParameters() throws Exception
   {
      ClassLoader loader = MockService.class.getClassLoader();
      final MockService internal = new MockService();

      MockService delegate = (MockService) Enhancer.create(MockService.class, new LazyLoader()
      {
         @Override
         public Object loadObject() throws Exception
         {
            return internal;
         }
      });
      MockService adapter = ClassLoaderAdapterCallback.enhance(loader, loader, delegate, MockService.class);
      Assert.assertNull(internal.echo(null));
      Assert.assertNull(adapter.echo(null));
   }
}