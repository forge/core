/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.integration;

/**
 * A priority key listener. KeyListeners are always called before the shell system processes the key strokes. This
 * provides the key listener the ability to override control.</p>
 * 
 * Note: KeyListeners should *not* be used for creating interactive prompts. Use
 * {@link org.jboss.forge.shell.Shell#scan()} instead. This facility if for overriding the default behavior of the shell
 * itself.
 * 
 * @author Mike Brock
 */
public interface KeyListener
{
   /**
    * Called for every keystroke in the shell.
    * 
    * @param key the character value
    * @return boolean indicating whether control is overridden (true prevents propagation; false is default behavior)
    */
   public boolean keyPress(int key);
}
