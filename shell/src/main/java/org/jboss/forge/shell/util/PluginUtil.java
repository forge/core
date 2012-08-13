/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.env.Configuration;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.Shell;
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

   public static List<PluginRef> findPlugin(final Shell shell, Configuration config, final String searchString)
            throws Exception
   {
      return findPlugin(shell, config, searchString, true);
   }

   public static List<PluginRef> findPluginSilent(final Shell shell, Configuration config, final String searchString)
            throws Exception
   {
      return findPlugin(shell, config, searchString, false);
   }

   public static List<PluginRef> findPlugin(final Shell shell, Configuration config, final String searchString,
            boolean speak) throws Exception
   {
      String defaultRepo = getDefaultRepo(shell.getEnvironment());

      InputStream repoStream = getCachedRepoStream(getDefaultRepo(shell.getEnvironment()), shell.getEnvironment());
      if (repoStream == null)
      {
         HttpGet httpGet = new HttpGet(defaultRepo);

         if (speak)
            shell.print("Connecting to remote repository [" + defaultRepo + "]... ");
         DefaultHttpClient client = new DefaultHttpClient();
         configureProxy(ProxySettings.fromForgeConfiguration(config), client);
         HttpResponse httpResponse = client.execute(httpGet);

         switch (httpResponse.getStatusLine().getStatusCode())
         {
         case 200:
            if (speak)
               shell.println("connected!");
            break;

         case 404:
            if (speak)
               shell.println("failed! (plugin index not found: " + defaultRepo + ")");
            return Collections.emptyList();

         default:
            if (speak)
               shell.println("failed! (server returned status code: "
                        + httpResponse.getStatusLine().getStatusCode());
            return Collections.emptyList();
         }

         repoStream = httpResponse.getEntity().getContent();
         setCachedRepoStream(defaultRepo, shell.getEnvironment(), repoStream);
         repoStream = getCachedRepoStream(getDefaultRepo(shell.getEnvironment()), shell.getEnvironment());
      }

      return getPluginsFromRepoStream(searchString, repoStream);
   }

   @SuppressWarnings("unchecked")
   private static void setCachedRepoStream(String repo, ForgeEnvironment environment, InputStream stream)
   {
      FileResource<?> cachedRepo = environment.getConfigDirectory().getChildOfType(FileResource.class,
               repo.replaceAll("[^a-zA-Z0-9]+", "") + ".yaml");
      if (!cachedRepo.exists())
      {
         cachedRepo.createNewFile();
      }

      cachedRepo.setContents(stream);
   }

   @SuppressWarnings("unchecked")
   private static InputStream getCachedRepoStream(String repo, ForgeEnvironment forgeEnvironment)
   {
      FileResource<?> cachedRepo = forgeEnvironment.getConfigDirectory().getChildOfType(FileResource.class,
               repo.replaceAll("[^a-zA-Z0-9]+", "") + ".yaml");
      if (cachedRepo.exists())
      {
         long lastModified = cachedRepo.getUnderlyingResourceObject().lastModified();
         if (System.currentTimeMillis() - lastModified <= 60000)
         {
            return cachedRepo.getResourceInputStream();
         }
         else
         {
            cachedRepo.delete();
         }
      }
      return null;
   }

   private static List<PluginRef> getPluginsFromRepoStream(final String searchString, InputStream stream)
   {
      List<PluginRef> pluginList = new ArrayList<PluginRef>();

      Yaml yaml = new Yaml();
      Pattern pattern = Pattern.compile(GeneralUtils.pathspecToRegEx("*" + searchString + "*"));
      for (Object o : yaml.loadAll(stream))
      {
         if (o == null)
         {
            continue;
         }

         @SuppressWarnings("unchecked")
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

   private static void configureProxy(final ProxySettings proxySettings, final DefaultHttpClient client)
   {
      if (proxySettings != null)
      {
         HttpHost proxy = new HttpHost(proxySettings.getProxyHost(), proxySettings.getProxyPort());
         client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

         if (proxySettings.isAuthenticationSupported())
         {
            AuthScope authScope = new AuthScope(proxySettings.getProxyHost(), proxySettings.getProxyPort());
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                     proxySettings.getProxyUserName(),
                     proxySettings.getProxyPassword());
            client.getCredentialsProvider().setCredentials(authScope, credentials);
         }
      }
   }

   public static void downloadFromURL(final PipeOut out, final URL url, final FileResource<?> resource)
            throws IOException
   {
      downloadFromURL(out, url, resource, true);
   }

   public static void downloadFromURL(final PipeOut out, final URL url, final FileResource<?> resource, boolean speak)
            throws IOException
   {

      HttpGet httpGetManifest = new HttpGet(url.toExternalForm());
      if (speak)
         out.print("Retrieving artifact ... ");

      HttpResponse response = new DefaultHttpClient().execute(httpGetManifest);
      switch (response.getStatusLine().getStatusCode())
      {
      case 200:
         if (speak)
            out.println("done.");
         try
         {
            resource.setContents(response.getEntity().getContent());
            if (speak)
               out.println("done.");
         }
         catch (IOException e)
         {
            if (speak)
               out.println("failed to download: " + e.getMessage());
         }

      default:
         if (speak)
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
