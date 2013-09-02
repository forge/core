/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.result;

import org.jboss.forge.addon.ui.UICommand;

/**
 * The result of a navigation
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface NavigationResult extends Result
{
   Class<? extends UICommand>[] getNext();
}
