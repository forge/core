/*
 * JBoss, by Red Hat.
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

package org.jboss.forge.shell.plugins.builtin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.UnknownFileResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.PluginUtil;

/**
 * @author Pablo Palazón
 */
@Alias("run-url")
@Topic("Shell Environment")
public class RunUrlPlugin implements Plugin
{
    private final Shell shell;

    @Inject
    private ResourceFactory factory;

    @Inject
    public RunUrlPlugin(final Shell shell)
    {
        this.shell = shell;
    }

    @DefaultCommand
    public void run(@Option(description = "url...", required = true) final String url, final PipeOut pipeOut,
                final String... args)
                throws Exception
    {
        String urlPattern = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        if (Pattern.matches(urlPattern, url))
        {
            URL remote = new URL(url);
            String temporalDir = System.getProperty("java.io.tmpdir");
            File tempFile = new File(temporalDir, "temp" + UUID.randomUUID().toString().replace("-", ""));
            tempFile.createNewFile();
            UnknownFileResource tempResource = new UnknownFileResource(factory, tempFile);
            PluginUtil.downloadFromURL(pipeOut, remote, tempResource);

            try
            {
                shell.execute(tempFile, args);
            }
            catch (IOException e)
            {
                throw new RuntimeException("error executing script from url " + url);
            }
        }
        else
        {
            throw new RuntimeException("resource must be a url: " + url);
        }
    }
}
