/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh;

import org.jboss.forge.container.services.Exported;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface ShellStreamProvider
{
   // Methods to do Stream configuration/get stream handles
    void setInputStream(InputStream input);

    InputStream getInputStream();

    void setOutputStream(OutputStream output);

    OutputStream getOutputStream();
}
