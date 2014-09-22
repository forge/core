package org.jboss.forge.addon.javaee.jpa.containers;

import org.jboss.forge.addon.javaee.jpa.JPADataSource;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.javaee.jpa.providers.Hibernate4Provider;
import org.jboss.forge.addon.javaee.jpa.providers.HibernateProvider;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;
import org.jboss.shrinkwrap.descriptor.api.persistence.PropertyCommon;

/**
 * Weblogic 12c Persistence Container
 *
 * @author Luca Masini
 *
 */
public class WebLogic12cContainer implements PersistenceContainer
{

   public static final String HIBERNATE_TRANSACTION_JTA_PLATFORM = "hibernate.transaction.jta.platform";
   public static final String WEBLOGIC_JTA_PLATFORM = "org.hibernate.service.jta.platform.internal.WeblogicJtaPlatform";

   @Override
   @SuppressWarnings("rawtypes")
   public PersistenceUnitCommon setupConnection(PersistenceUnitCommon unit,
            JPADataSource dataSource)
   {
      unit.transactionType("JTA");
      unit.jtaDataSource(dataSource.getJndiDataSource());

      if (HibernateProvider.JPA_PROVIDER.equals(unit.getProvider())
               || Hibernate4Provider.JPA_PROVIDER.equals(unit.getProvider()))
      {
         PropertyCommon property = unit.getOrCreateProperties()
                  .createProperty();
         property.name(HIBERNATE_TRANSACTION_JTA_PLATFORM).value(WEBLOGIC_JTA_PLATFORM);
      }

      return unit;
   }

   @Override
   public void validate(JPADataSource dataSource) throws Exception
   {
      if ((dataSource.getJndiDataSource() == null) || dataSource.getJndiDataSource().trim().isEmpty())
      {
         throw new RuntimeException("Must specify a JTA data-source.");
      }
   }

   @Override
   public boolean isDataSourceRequired()
   {
      return true;
   }

   @Override
   public String getName(boolean isGUI)
   {
      return isGUI ? "Oracle Weblogic 12c" : "WEBLOGIC_12C";
   }
}
