/*
 *
 *  * JBoss, Home of Professional Open Source
 *  * Copyright 2011, Red Hat, Inc., and individual contributors
 *  * by the @authors tag. See the copyright.txt in the distribution for a
 *  * full listing of individual contributors.
 *  *
 *  * This is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU Lesser General Public License as
 *  * published by the Free Software Foundation; either version 2.1 of
 *  * the License, or (at your option) any later version.
 *  *
 *  * This software is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  * Lesser General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public
 *  * License along with this software; if not, write to the Free
 *  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */

package org.jboss.forge.plugins;

import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.shrinkwrap.descriptor.spi.Node;

import javax.inject.Inject;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */

@Alias("plugin-repo")
public class PluginRepository implements Plugin
{
   @Inject Shell shell;

   @Command("install")
   public void installPlugin()
   {
      try
      {
         URL url = new URL("http://jboss-forge.s3.amazonaws.com/pluginrepo.xml");
         //URL url = new URL("http://localhost/~paul/pluginrepo.xml");
         InputStream inputStream = url.openStream();
         Node parse = XMLParser.parse(inputStream);
         Map<String, Node> categories = new HashMap<String, Node>();

         for (Node node : parse.children())
         {
            String name = node.attribute("name");
            categories.put(name, node);
         }

         Node category = shell.promptChoice("From what group do you want to install?", categories);
         Map<String, Node> pluginsChoices = new HashMap<String, Node>();
         if (category.getSingle("groups") != null && category.getSingle("groups").get("group") != null)
         {
            List<Node> pluginGroups = category.getSingle("groups").get("group");
            for (Node pluginGroup : pluginGroups)
            {
               pluginsChoices.put(pluginGroup.attribute("name"), pluginGroup);
            }
         }

         if (category.getSingle("plugins") != null && category.get("plugin") != null)
         {
            List<Node> plugins = category.getSingle("plugins").get("plugin");
            for (Node plugin : plugins)
            {
               pluginsChoices.put(plugin.getSingle("name").text(), plugin);
            }
         }

         Node plugin = shell.promptChoice("Which plugin do you want to install?", pluginsChoices);
         pluginsChoices.clear();

         if (plugin.name().equals("group"))
         {
            List<Node> groupedPlugins = plugin.get("plugin");
            for (Node pluginInGroup : groupedPlugins)
            {
               pluginsChoices.put(pluginInGroup.getSingle("name").text(), pluginInGroup);
            }

            plugin = shell.promptChoice("Which plugin do you want to install?", pluginsChoices);
         }

         shell.execute("forge git-plugin " + plugin.getSingle("url").text());
      } catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
