package org.jboss.forge.spec.javaee.jpa.container;

import javax.inject.Inject;

import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.spec.javaee.jpa.api.JPADataSource;
import org.jboss.forge.spec.javaee.jpa.api.PersistenceContainer;
import org.jboss.forge.spec.javaee.jpa.provider.HibernateProvider;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.TransactionType;

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

   @Inject
   private ShellPrintWriter writer;

   @Override
   public PersistenceUnitDef setupConnection(final PersistenceUnitDef unit, final JPADataSource dataSource)
   {
      if (HibernateProvider.JPA_PROVIDER.equals(unit.getProvider()))
      {
         unit.property(HIBERNATE_TRANSACTION_JTA_PLATFORM, WEBLOGIC_JTA_PLATFORM);
      }

      unit.transactionType(TransactionType.JTA);
      if ((dataSource.getJndiDataSource() == null) || dataSource.getJndiDataSource().trim().isEmpty())
      {
         throw new RuntimeException("Must specify a JTA data-source.");
      }

      if (dataSource.hasJdbcConnectionInfo())
      {
         ShellMessages.info(writer, "Ignoring jdbc connection info [" + dataSource.getJdbcConnectionInfo() + "]");
      }

      unit.jtaDataSource(dataSource.getJndiDataSource());
      unit.nonJtaDataSource(null);

      return unit;
   }

   @Override
   public TransactionType getTransactionType()
   {
      return TransactionType.JTA;
   }
}
