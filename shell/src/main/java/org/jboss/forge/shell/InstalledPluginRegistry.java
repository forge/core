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
package org.jboss.forge.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.parser.xml.XMLParserException;
import org.jboss.forge.shell.util.OSUtils;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class InstalledPluginRegistry
{

   public static List<String> getInstalledPlugins()
   {
      List<String> result = new ArrayList<String>();
      File registryFile = new File(OSUtils.getUserHomePath() + "/.forge/plugins/installed.xml");
      try {
         Node installed = XMLParser.parse(new FileInputStream(registryFile));
         List<Node> list = installed.get("plugin");
         for (Node plugin : list) {
            result.add(plugin.getText());
         }
      }
      catch (XMLParserException e) {
         throw new RuntimeException("Invalid syntax in [" + registryFile.getAbsolutePath()
                  + "] - Please delete this file and restart Forge", e);
      }
      catch (FileNotFoundException e) {
         // this is OK, no plugins installed
      }
      return result;
   }

}
