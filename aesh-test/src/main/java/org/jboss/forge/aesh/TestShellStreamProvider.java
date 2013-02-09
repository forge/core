/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class TestShellStreamProvider implements ShellStreamProvider
{
    private InputStream input;
    private OutputStream output;

    public void setInputStream(InputStream input) {
        this.input = input;
    }

    public InputStream getInputStream() {
        return input;
    }

    public void setOutputStream(OutputStream output) {
        this.output = output;
    }

    public OutputStream getOutputStream() {
        return output;
    }
}
