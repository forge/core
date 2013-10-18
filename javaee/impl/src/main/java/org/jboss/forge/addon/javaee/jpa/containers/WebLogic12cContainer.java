package org.jboss.forge.addon.javaee.jpa.containers;

import org.jboss.forge.addon.javaee.jpa.JPADataSource;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.javaee.jpa.providers.HibernateProvider;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceUnit;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceUnitTransactionType;
import org.jboss.shrinkwrap.descriptor.api.persistence20.Properties;
import org.jboss.shrinkwrap.descriptor.api.persistence20.Property;

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
   public PersistenceUnit<PersistenceDescriptor> setupConnection(PersistenceUnit<PersistenceDescriptor> unit,
            JPADataSource dataSource)
   {
      if (HibernateProvider.JPA_PROVIDER.equals(unit.getProvider()))
      {
         Property<Properties<PersistenceUnit<PersistenceDescriptor>>> property = unit.getOrCreateProperties()
                  .createProperty();
         property.name(HIBERNATE_TRANSACTION_JTA_PLATFORM).value(WEBLOGIC_JTA_PLATFORM);
      }

      unit.transactionType(PersistenceUnitTransactionType._JTA);
      unit.jtaDataSource(dataSource.getJndiDataSource());
      unit.nonJtaDataSource(null);

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
   public boolean isJTASupported()
   {
      return true;
   }

   @Override
   public String getName(boolean isGUI)
   {
      return isGUI ? "Oracle Weblogic 12c" : "WEBLOGIC_12C";
   }
}
