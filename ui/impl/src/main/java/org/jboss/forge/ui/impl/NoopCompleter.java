/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.impl;

import java.util.Collections;

import org.jboss.forge.ui.input.InputComponent;
import org.jboss.forge.ui.input.UICompleter;

/**
 * A {@link UICompleter} that always returns zero proposals.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@SuppressWarnings("rawtypes")
public class NoopCompleter implements UICompleter
{
   @Override
   public Iterable<String> getCompletionProposals(InputComponent input, String value)
   {
      return Collections.emptyList();
   }
}
