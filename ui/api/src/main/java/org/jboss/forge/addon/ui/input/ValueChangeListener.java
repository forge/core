/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

import java.util.EventListener;

import org.jboss.forge.addon.ui.input.events.ValueChangeEvent;

/**
 * Listen for value change events
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ValueChangeListener extends EventListener
{
   public void valueChanged(ValueChangeEvent event);
}
