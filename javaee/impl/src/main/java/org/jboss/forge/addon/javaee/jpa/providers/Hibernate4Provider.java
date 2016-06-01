/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.providers;

/**
 * Hibernate 4.x provider
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class Hibernate4Provider extends AbstractHibernateProvider
{
   public static final String JPA_PROVIDER = "org.hibernate.jpa.HibernatePersistenceProvider";

   @Override
   public String getProvider()
   {
      return JPA_PROVIDER;
   }

   @Override
   public String getName()
   {
      return "Hibernate 4.x";
   }
}
