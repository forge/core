/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

import java.lang.annotation.Annotation;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public enum LifecycleType
{
   PRE_PERSIST(PrePersist.class),
   POST_PERSIST(PostPersist.class),
   PRE_UPDATE(PreUpdate.class),
   POST_UPDATE(PostUpdate.class),
   PRE_REMOVE(PreRemove.class),
   POST_REMOVE(PostRemove.class),
   POST_LOAD(PostLoad.class);

   private LifecycleType(Class<? extends Annotation> annotation)
   {
      this.annotation = annotation;
   }

   private Class<? extends Annotation> annotation;

   public Class<? extends Annotation> getAnnotation()
   {
      return annotation;
   }
}
