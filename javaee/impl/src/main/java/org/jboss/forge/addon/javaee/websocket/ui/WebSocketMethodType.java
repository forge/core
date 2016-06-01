/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.websocket.ui;

import java.lang.annotation.Annotation;

import javax.websocket.*;

/**
 * WebSocket Method Type
 *
 * @author <a href="mailto:lantonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public enum WebSocketMethodType
{

   ON_OPEN(OnOpen.class, new Class[] { Session.class }),

   ON_CLOSE(OnClose.class, new Class[] { Session.class, CloseReason.class }),

   ON_MESSAGE(OnMessage.class, new Class[] { Session.class, String.class }),

   ON_ERROR(OnError.class, new Class[] { Throwable.class });

   private Class<? extends Annotation> annotation;
   private Class[] parameters;

   WebSocketMethodType(Class<? extends Annotation> annotation, Class[] parameters)
   {
      this.annotation = annotation;
      this.parameters = parameters;
   }

   public Class<? extends Annotation> getAnnotation()
   {
      return annotation;
   }

   public Class[] getParameters()
   {
      return parameters;
   }
}
