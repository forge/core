/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.bus;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.bus.cdi.ObserverCaptureExtension;
import org.jboss.forge.bus.event.BusEvent;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@RunWith(Arquillian.class)
public class EventBusTest
{
   @Deployment
   public static JavaArchive createTestArchive()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar")
               .addClass(ObserverCaptureExtension.class)
               .addClass(MockEventObserver.class)
               .addClass(EventBus.class)
               .addClass(BusEvent.class)
               .addAsManifestResource("META-INF/services/javax.enterprise.inject.spi.Extension")
               .addAsManifestResource(new ByteArrayAsset("<beans/>".getBytes()), ArchivePaths.create("beans.xml"));
   }

   @Inject
   private EventBus bus;

   @Inject
   private MockEventObserver observer;

   @Test
   public void testEventsQueued() throws Exception
   {
      bus.enqueue(new MockEvent());
      bus.enqueue(new MockEvent());
      bus.enqueue(new MockEvent(), new Annotation[] { new AnnotationLiteral<Special>()
      {
         private static final long serialVersionUID = -6035326874728801791L;
      } });

      assertEquals(0, observer.getCount());
      assertEquals(0, observer.getCountSpecial());
      bus.fireAll();
      assertEquals(3, observer.getCount());
      assertEquals(1, observer.getCountSpecial());
   }
}
