/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.bus;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
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
               .addClass(EventBus.class)
               .addClass(EventBusQueuedException.class)
               .addClass(MockEvent.class)
               .addClass(MockEventObserver.class)
               .addManifestResource(new ByteArrayAsset("<beans/>".getBytes()), ArchivePaths.create("beans.xml"));
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
