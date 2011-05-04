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

package org.jboss.forge.shell.plugins.builtin;

import javax.inject.Inject;

import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.plugins.DefaultCommand;

/**
 * @author <a href="mailto:rdruss@gmail.com">Rodney Russ</a>
 */
@Alias("pwr") 
@Topic("File & Resources")
@Help("Prints the current working resource.")
public class PwrPlugin implements org.jboss.forge.shell.plugins.Plugin {

    @Inject 
    private Shell shell;

    @DefaultCommand 
    public void run() {
        shell.println(shell.getCurrentResource().getFullyQualifiedName());
    }
}
