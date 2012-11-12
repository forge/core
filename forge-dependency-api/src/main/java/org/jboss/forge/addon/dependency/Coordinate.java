/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency;

/**
 * Marker interface for a Coordinate object.
 *
 * Implementations should provide additional methods about how to locate an artifact.
 *
 * Eg: If using Maven, the {@link Coordinate} implementation should return the artifact's groupId, artifactId and
 * version
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface Coordinate
{

}
