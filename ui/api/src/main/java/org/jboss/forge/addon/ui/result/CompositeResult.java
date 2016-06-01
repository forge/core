/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.result;

import java.util.List;

/**
 * A {@link CompositeResult} is composed of several {@link Result} objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface CompositeResult extends Result
{
   List<Result> getResults();
}
