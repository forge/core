/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.bus.spi;

import java.util.List;

import org.jboss.forge.bus.EventBus;

/**
 * Service enabling classes to groom the {@link EventBus} before any events are fired via {@link EventBus#fireAll()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface EventBusGroomer
{
   List<Object> groom(List<Object> events);
}
