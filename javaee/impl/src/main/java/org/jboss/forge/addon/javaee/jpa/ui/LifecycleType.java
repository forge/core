/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui;

import javax.persistence.*;

public enum LifecycleType
{
   PRE_PERSIST(PrePersist.class),
   POST_PERSIST(PostPersist.class),
   PRE_UPDATE(PreUpdate.class),
   POST_UPDATE(PostUpdate.class),
   PRE_REMOVE(PreRemove.class),
   POST_REMOVE(PostRemove.class),
   POST_LOAD(PostLoad.class);

   private LifecycleType(Class annotation)
   {
      this.annotation = annotation;
   }

   private Class annotation;

   public Class getAnnotation()
   {
      return annotation;
   }
}
