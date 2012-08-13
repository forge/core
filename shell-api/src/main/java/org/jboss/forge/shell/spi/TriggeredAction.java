/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.spi;

import java.awt.event.ActionListener;

/**
 * Allows the shell to register an ActionListener that is performed when the corresponding character is being read by
 * the ConsoleReader.
 * 
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 * 
 */
public interface TriggeredAction
{
   /**
    * The char that is used to register the ActionListener as a TriggeredAction in the Shell.
    */
   public char getTrigger();

   /**
    * The ActionListener for which the actionPerformed method will be invoked when the trigger character is read.
    */
   public ActionListener getListener();
}
