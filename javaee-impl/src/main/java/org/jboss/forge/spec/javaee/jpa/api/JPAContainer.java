/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jpa.api;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.shell.util.BeanManagerUtils;
import org.jboss.forge.spec.javaee.jpa.container.*;

/**
 * Represents the list of known/supported JPA container types.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public enum JPAContainer
{
   JBOSS_AS6(JBossAS6Container.class),
   JBOSS_AS7(JBossAS7Container.class),
   JBOSS_EAP6(JBossAS7Container.class),
   GLASSFISH_3(GlassFish3Container.class),
   CUSTOM_JDBC(CustomJDBCContainer.class),
   CUSTOM_JTA(CustomJTAContainer.class),
   CUSTOM_NON_JTA(NonJTAContainer.class),
   WEBLOGIC_12C(WebLogic12cContainer.class);


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
