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

package org.jboss.forge.shell.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.ShellImpl;
import org.jboss.forge.shell.plugins.PipeOut;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Mike Brock .
 */
public class PluginUtil
{
   private static final String PROP_GIT_REPOSITORY = "gitrepo";
   private static final String PROP_HOME_MAVEN_REPO = "homerepo";
   private static final String PROP_ARTIFACT = "artifact";
   private static final String PROP_DESCRIPTION = "description";
   private static final String PROP_AUTHOR = "author";
   private static final String PROP_NAME = "name";
   private static final String PROP_WEBSITE = "website";
   private static final String PROP_GIT_REF = "gitref";
   private static final String PROP_TAGS = "tags";

   private static String getDefaultRepo(final ForgeEnvironment environment)
   {
      String defaultRepo = (String) environment.getProperty(ShellImpl.PROP_DEFAULT_PLUGIN_REPO);
      if (defaultRepo == null)
      {
         throw new RuntimeException("no default repository set: (to set, type: set "
                  + ShellImpl.PROP_DEFAULT_PLUGIN_REPO + " <repository>)");
      }
      return defaultRepo;
   }

   @SuppressWarnings("unchecked")
   public static List<PluginRef> findPlugin(final ForgeEnvironment environment, final String searchString,
            final PipeOut out)
            throws Exception
   {
      String defaultRepo = getDefaultRepo(environment);
      HttpGet httpGet = new HttpGet(defaultRepo);

      out.print("Connecting to remote repository [" + defaultRepo + "]... ");
      HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);

      switch (httpResponse.getStatusLine().getStatusCode())
      {
      case 200:
         out.println("connected!");
         break;

      case 404:
         out.println("failed! (plugin index not found: " + defaultRepo + ")");
         return Collections.emptyList();

      default:
         out.println("failed! (server returned status code: " + httpResponse.getStatusLine().getStatusCode());
         return Collections.emptyList();
      }

      Pattern pattern = Pattern.compile(GeneralUtils.pathspecToRegEx("*" + searchString + "*"));

      List<PluginRef> pluginList = new ArrayList<PluginRef>();

      Yaml yaml = new Yaml();
      // TODO this needs to be cached instead of downloaded each time
      for (Object o : yaml.loadAll(httpResponse.getEntity().getContent()))
      {
         if (o == null)
         {
            continue;
         }

         Map<String, String> map = (Map<String, String>) o;

         PluginRef ref = bindToPuginRef(map);
         if (pattern.matcher(ref.getName()).matches() || pattern.matcher(ref.getDescription()).matches()
                  || pattern.matcher(ref.getTags()).matches())
         {
            pluginList.add(ref);
         }
      }

      return pluginList;
   }

   public static void downloadFromURL(final PipeOut out, final URL url, final FileResource<?> resource)
            throws IOException
   {

      HttpGet httpGetManifest = new HttpGet(url.toExternalForm());
      out.print("Retrieving artifact ... ");

      HttpResponse response = new DefaultHttpClient().execute(httpGetManifest);
      switch (response.getStatusLine().getStatusCode())
      {
      case 200:
         out.println("done.");
         try
         {
            resource.setContents(response.getEntity().getContent());
            out.println("done.");
         }
         catch (IOException e)
         {
            out.println("failed to download: " + e.getMessage());
         }

      default:
         out.println("failed! (server returned status code: " + response.getStatusLine().getStatusCode());
      }
   }

   private static PluginRef bindToPuginRef(final Map<String, String> map)
   {
      PluginRef ref = new PluginRef();
      ref.setName(map.get(PROP_NAME));
      ref.setWebsite(map.get(PROP_WEBSITE));
      ref.setArtifact(map.get(PROP_ARTIFACT));
      ref.setAuthor(map.get(PROP_AUTHOR));
      ref.setDescription(map.get(PROP_DESCRIPTION));
      ref.setTags(map.get(PROP_TAGS));
      ref.setHomeRepo(map.get(PROP_HOME_MAVEN_REPO));
      ref.setGitRepo(map.get(PROP_GIT_REPOSITORY));
      ref.setGitRef(map.get(PROP_GIT_REF));
      return ref;
   }
}
