/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.shell.test.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.events.CommandExecuted;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 */
@RunWith(Arquillian.class)
public class CommandExecutedTest extends AbstractShellTest {
	
	@Inject CommandExecutedObserver observer;

	@Test
	public void testInvalidSuppliedOptionIsCorrected() throws Exception
	{
	    getShell().execute("motp motp");
	    CommandExecuted event = observer.getEvent();
	    assertNotNull(event);
	    assertEquals(CommandExecuted.Status.SUCCESS, event.getStatus());
	    CommandMetadata command = event.getCommand();
	    assertNotNull(command);
	    assertEquals("motp", command.getName());
	    Object[] parameters = event.getParameters();
	    assertNotNull(parameters);
	    assertEquals(1, parameters.length);
	    assertEquals("motp", parameters[0]);
	}

}
