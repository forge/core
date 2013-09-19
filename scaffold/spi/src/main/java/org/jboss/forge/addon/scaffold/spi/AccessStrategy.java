/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.spi;

import java.util.List;

import org.jboss.forge.addon.resource.Resource;

/**
 * Defines how a web-user will interact with a given web-resource.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface AccessStrategy
{
   public List<String> getWebPaths(Resource<?> r);

   public Resource<?> fromWebPath(String path);
}
