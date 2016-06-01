/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.mock.command;

import org.jboss.forge.addon.ui.annotation.Command;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ThrowExceptionCommand
{
   @Command("throw-exception")
   public String throwIt()
   {
      throw new UnsupportedOperationException("Intentional failure.");
   }
}
