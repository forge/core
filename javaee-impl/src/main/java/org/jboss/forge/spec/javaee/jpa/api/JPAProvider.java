package org.jboss.forge.spec.javaee.jpa.api;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.shell.util.BeanManagerUtils;
import org.jboss.forge.spec.javaee.jpa.provider.EclipseLinkProvider;
import org.jboss.forge.spec.javaee.jpa.provider.HibernateProvider;
import org.jboss.forge.spec.javaee.jpa.provider.OpenJPAProvider;

public enum JPAProvider
{
   HIBERNATE(HibernateProvider.class),
   OPENJPA(OpenJPAProvider.class),
   ECLIPSELINK(EclipseLinkProvider.class);

   private Class<? extends PersistenceProvider> type;

   private JPAProvider(Class<? extends PersistenceProvider> type)
   {
      this.type = type;
   }

   public PersistenceProvider getProvider(BeanManager manager)
   {
      return BeanManagerUtils.getContextualInstance(manager, type);
   }
}
