/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.api;

/**
 * The user input for an UI
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <T>
 */
public interface Input<T>
{
   InputDefinition<T> getDefinition();

   T getValue();
}
