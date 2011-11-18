/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.shell.test.plugins.builtin;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.SetupCommand;

/**
 * @author <a href="mailto:sachsedaniel@gmail.com">Daniel 'Wombat' Sachse</a>
 * 
 */
@Alias("testplugin2")
@RequiresFacet({ MockFacet.class })
public class SetupCommandMockPlugin2 implements Plugin {
	@Inject
	private Event<InstallFacets> install;

	@SetupCommand
	public void setup() {
		install.fire(new InstallFacets(MockFacet2.class));
	}

	@Command
	public void other() {

	}

	@DefaultCommand
	public void deflt() {}
}