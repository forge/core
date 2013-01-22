/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.impl;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.ui.UIInputCompleter;

/**
 * A {@link UIInputCompleter} that always returns zero proposals.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@SuppressWarnings("rawtypes")
public class NoopCompleter implements UIInputCompleter
{
   @Override
   public List getCompletionProposals(String value)
   {
      return new ArrayList();
   }
}
