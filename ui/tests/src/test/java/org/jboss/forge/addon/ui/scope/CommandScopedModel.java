/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.scope;

import org.jboss.forge.addon.ui.cdi.CommandScoped;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@CommandScoped
public class CommandScopedModel
{
   private String name;

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }
}
