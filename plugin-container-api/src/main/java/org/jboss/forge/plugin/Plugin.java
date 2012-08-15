/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin;

/**
 * A custom {@link Plugin} must implement this interface in order to be detected and installed at framework boot-time.
 * In order to create plugin shell-commands, one must create a method annotated with @{@link Command}. Any command
 * method parameters to be provided as input through the shell must be individually annotated with the @{@link Option}
 * annotation; other (non-annotated) command parameters are ignored.
 * <p/>
 * In order to control the name of a custom plugin, the {@link Alias} annotation may be added to any {@link Plugin}
 * type.
 * <p/>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Plugin
{

}
