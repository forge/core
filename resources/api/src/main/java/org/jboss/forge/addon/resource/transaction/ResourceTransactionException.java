/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.transaction;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceException;

/**
 * Represents a transaction exception thrown by the {@link Resource} API
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ResourceTransactionException extends ResourceException
{
   private static final long serialVersionUID = 1L;

   public ResourceTransactionException()
   {
      super("No message");
   }

   public ResourceTransactionException(String message, Throwable e)
   {
      super(message, e);
   }

   public ResourceTransactionException(String message)
   {
      super(message);
   }
}
