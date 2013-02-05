/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.commands;

import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.base.UICommandMetadataBase;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class QuitCommand extends BaseExitCommand
{
   @Override
   public UICommandMetadata getMetadata()
   {
      return new UICommandMetadataBase("quit", "Exit the shell");
   }
}
