/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.bus.cdi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.bus.MockEvent;
import org.jboss.forge.bus.event.BusEvent;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ObserverCaptureExtensionTest
{
   @Deployment
   public static JavaArchive createTestArchive()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar")
               .addClass(ObserverCaptureExtension.class)
               .addClass(MockBaseEventObserver.class)
               .addClass(BusEvent.class)
               .addAsManifestResource("META-INF/services/javax.enterprise.inject.spi.Extension")
               .addAsManifestResource(new ByteArrayAsset("<beans/>".getBytes()), ArchivePaths.create("beans.xml"));
   }

   MockEvent event = new MockEvent();

   @Inject
   private BeanManager manager;

   @Inject
   private MockBaseEventObserver observer;

   @Inject
   private ObserverCaptureExtension oce;

   @Before
   public void reset()
   {
      observer.setObservedNormal(false);
      observer.setObservedRemoved(false);
      observer.setObservedRemoved2(false);
   }

   @Test
   public void testRemovedObserversDoNotObserve() throws Exception
   {
      assertFalse(observer.hasObservedRemoved());
      manager.fireEvent(event, new Annotation[] {});
      assertFalse(observer.hasObservedRemoved());
   }

   @Test
   public void testRemovedObserversInvokeViaQualifiedEvent() throws Exception
   {
      assertFalse(observer.hasObservedRemoved());
      List<BusManaged> qualifiers = oce.getEventQualifiers(event.getClass());
      BusManaged busManaged = qualifiers.get(0);
      manager.fireEvent(event, new Annotation[] { busManaged });
      assertTrue(observer.hasObservedRemoved());
   }

   @Test
   public void testRemovedObserversInvokeUniquelyViaQualifiedEvent() throws Exception
   {
      assertFalse(observer.hasObservedRemoved());
      List<BusManaged> qualifiers = oce.getEventQualifiers(event.getClass());
      BusManaged busManaged = qualifiers.get(0);
      manager.fireEvent(event, new Annotation[] { busManaged });
      assertTrue(observer.hasObservedRemoved());

      assertFalse(observer.hasObservedRemoved2());
   }

   @Test
   public void testNormalObserversContinueToObserve() throws Exception
   {
      assertFalse(observer.hasObservedNormal());
      manager.fireEvent(new MockNormalEvent(), new Annotation[] {});
      assertTrue(observer.hasObservedNormal());
   }
}
