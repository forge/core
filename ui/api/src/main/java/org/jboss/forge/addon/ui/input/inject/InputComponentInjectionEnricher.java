/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input.inject;

import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * Enriches an injected {@link InputComponent}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface InputComponentInjectionEnricher
{
   public void enrich(InputComponentInjectionPoint injectionPoint, InputComponent<?, ?> input);

}
