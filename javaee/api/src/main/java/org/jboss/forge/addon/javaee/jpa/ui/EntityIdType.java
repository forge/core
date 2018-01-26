/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

/**
 * Describes what kind of ID an Entity uses.
 *
 * @author <a href="mailto:ch.schulz@joinout.de">Christoph "criztovyl" Schulz</a>
 */
public enum EntityIdType {
    /**
     * Use an {@link javax.persistence.IdClass}.
     */
    ID_CLASS,
    /**
     * Use an {@link javax.persistence.EmbeddedId}.
     */
    EMBEDDED_ID;
}

