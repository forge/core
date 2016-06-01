/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;


/**
 * Provides a {@link UICompleter} object
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <IMPL>
 * @param <VALUETYPE>
 */
public interface HasCompleter<IMPL, VALUETYPE>
{
   UICompleter<VALUETYPE> getCompleter();

   IMPL setCompleter(UICompleter<VALUETYPE> completer);

}
