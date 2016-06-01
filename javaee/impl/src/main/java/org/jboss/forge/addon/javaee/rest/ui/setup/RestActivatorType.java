/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.ui.setup;

import javax.ws.rs.core.Application;

/**
 * Configuration types of JAX-RS
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public enum RestActivatorType
{
   /**
    * Configured via an {@link Application}
    */
   APP_CLASS("Application class"),

   /**
    * Configured via web.xml
    */
   WEB_XML("Web Descriptor file (WEB.XML)");

   private final String description;

   private RestActivatorType(String description)
   {
      this.description = description;
   }

   /**
    * @return the description
    */
   public String getDescription()
   {
      return description;
   }
}
