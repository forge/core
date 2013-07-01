/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.request;

import java.util.List;

/**
 * A {@link InstallRequest} delegates a group of requests so that it may be performed in a single call
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface InstallRequest extends AddonActionRequest
{
   public List<AddonActionRequest> getActions();
}
