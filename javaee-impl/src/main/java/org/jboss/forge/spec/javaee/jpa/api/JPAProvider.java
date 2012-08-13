/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jpa.api;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.shell.util.BeanManagerUtils;
import org.jboss.forge.spec.javaee.jpa.provider.EclipseLinkProvider;
import org.jboss.forge.spec.javaee.jpa.provider.HibernateProvider;
import org.jboss.forge.spec.javaee.jpa.provider.InfinispanProvider;
import org.jboss.forge.spec.javaee.jpa.provider.OpenJPAProvider;

/**
 * Represents the list of known/supported JPA implementation providers
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public enum JPAProvider
{
   HIBERNATE(HibernateProvider.class),
   OPENJPA(OpenJPAProvider.class),
   ECLIPSELINK(EclipseLinkProvider.class),
   INFINISPAN(InfinispanProvider.class);

   private Class<? extends PersistenceProvider> type;

   private JPAProvider(final Class<? extends PersistenceProvider> type)
   {
      this.type = type;
   }

   public PersistenceProvider getProvider(final BeanManager manager)
   {
      return BeanManagerUtils.getContextualInstance(manager, type);
   }
}
