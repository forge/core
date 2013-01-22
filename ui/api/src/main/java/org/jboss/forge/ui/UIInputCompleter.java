/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui;

import java.util.List;

/**
 * @param VALUETYPE The value type to be provided by completion.
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface UIInputCompleter<VALUETYPE>
{
   // FIXME this needs to be thought out, before or after validation?
   // Should this take a String or the actual VALUETYPE instead?
   List<VALUETYPE> getCompletionProposals(String value);
}
