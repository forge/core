/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.validation;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ManagedObject
{
   public String sayHello(@NotNull String name)
   {
      return "Hello," + name;
   }
}
