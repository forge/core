package org.jboss.forge.spec.javaee.jpa.api;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.shell.util.BeanManagerUtils;
import org.jboss.forge.spec.javaee.jpa.container.CustomJDBCContainer;
import org.jboss.forge.spec.javaee.jpa.container.CustomJTAContainer;
import org.jboss.forge.spec.javaee.jpa.container.GlassFish3Container;
import org.jboss.forge.spec.javaee.jpa.container.JBossAS6Container;
import org.jboss.forge.spec.javaee.jpa.container.JBossAS7Container;
import org.jboss.forge.spec.javaee.jpa.container.NonJTAContainer;

public enum JPAContainer
{
   JBOSS_AS6(JBossAS6Container.class),
   JBOSS_AS7(JBossAS7Container.class),
   GLASSFISH_3(GlassFish3Container.class),
   CUSTOM_JDBC(CustomJDBCContainer.class),
   CUSTOM_JTA(CustomJTAContainer.class),
   CUSTOM_NON_JTA(NonJTAContainer.class);

   private Class<? extends PersistenceContainer> containerType;

   private JPAContainer(final Class<? extends PersistenceContainer> containerType)
   {
      this.containerType = containerType;
   }

   public PersistenceContainer getContainer(final BeanManager manager)
   {
      return BeanManagerUtils.getContextualInstance(manager, containerType);
   }
}
