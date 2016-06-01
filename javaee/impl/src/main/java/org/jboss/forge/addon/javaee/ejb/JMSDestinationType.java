/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.ejb;

/**
 * @author <a href="mailto:fiorenzo.pizza@gmail.com">fiorenzo pizza</a>
 */

public enum JMSDestinationType
{
   QUEUE("javax.jms.Queue"), TOPIC("javax.jms.Topic");

   private String destinationType;

   private JMSDestinationType(String destinationType)
   {
      this.destinationType = destinationType;
   }

   public String getDestinationType()
   {
      return destinationType;
   }
}
