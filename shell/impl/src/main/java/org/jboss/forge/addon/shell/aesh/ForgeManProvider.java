/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import org.jboss.aesh.console.helper.ManProvider;

import java.io.InputStream;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ForgeManProvider implements ManProvider {
    @Override
    public InputStream getManualDocument(String command) {
        return null;
    }
}
