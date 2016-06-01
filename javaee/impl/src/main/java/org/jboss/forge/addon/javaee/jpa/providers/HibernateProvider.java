/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.providers;

/**
 * Hibernate provider
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class HibernateProvider extends AbstractHibernateProvider
{
   public static final String JPA_PROVIDER = "org.hibernate.ejb.HibernatePersistence";

   @Override
   public String getProvider()
   {
      return JPA_PROVIDER;
   }

   @Override
   public String getName()
   {
      return "Hibernate";
   }
}
