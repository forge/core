/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.spi;

import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.forge.container.services.Exported;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface ShellStreamProvider
{
   InputStream getInputStream();

   OutputStream getStdOut();

   OutputStream getStdErr();
   
   void reset();
}
