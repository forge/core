/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.events;

import org.jboss.forge.shell.Shell;

/**
 * Fired by the {@link Shell} when it has successfully started up and is ready to accept user input. If plugins need to
 * take action before the user is given a chance to interact, they should observe this event.
 * <p>
 * <strong>For example:</strong>
 * <p>
 * <code>public void myObserver(@Observes {@link PostStartup} event)<br/>
 * {<br/>
 *    // do something<br/>
 * }<br/>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public final class PostStartup
{

}
