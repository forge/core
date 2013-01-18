/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.tracking;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.env.Configuration;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.events.PostStartup;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.util.OSUtils;
import org.jboss.forge.shell.util.Streams;

import com.dmurph.tracking.AnalyticsConfigData;
import com.dmurph.tracking.JGoogleAnalyticsTracker;
import com.dmurph.tracking.JGoogleAnalyticsTracker.GoogleAnalyticsVersion;

@Alias("analytics")
public class GoogleAnalyticsPlugin implements Plugin
{
   private static final String ANALYTICS_ENABLED = "forge.analytics.enabled";
   private static final String ANALYTICS_LAST_RUN = "forge.analytics.lastrun";

   private static final String ANALYTICS_DATE_MASK = "yyyyMMdd";

   public static final String VALUE_NO_REFERRAL = "0";

   @Inject
   private Configuration configuration;

   @Inject
   private Shell shell;

   @Inject
   private ForgeEnvironment environment;

   private JGoogleAnalyticsTracker analyticsTracker;

   /**
    * Google Analytics key used for tracking Forge installations
    */
   private static final String FORGE_GOOGLE_ANALYTICS_KEY = System.getProperty("forge.google_analytics.key",
            "UA-34467975-2");

   public void onStartup(@Observes PostStartup startup)
   {
      Boolean enabled = configuration.getBoolean(ANALYTICS_ENABLED, null);
      if (enabled == null)
      {
         if (Boolean.getBoolean("forge.analytics.no_prompt"))
         {
            enabled = Boolean.FALSE;
         }
         else
         {
            enabled = shell
                     .promptBoolean(
                              "Will you allow the Forge team to receive anonymous usage statistics for this instance of JBoss Forge?",
                              false);
         }
         configuration.setProperty(ANALYTICS_ENABLED, enabled);
      }
      setEnabled(enabled);
      if (enabled && isAllowedToRun())
      {
         sendInfoToAnalytics();
      }
   }

   /**
    *
    * @return
    */
   boolean isAllowedToRun()
   {
      boolean ret = true;
      String lastRunDate = configuration.getString(ANALYTICS_LAST_RUN, null);
      if (lastRunDate != null)
      {
         SimpleDateFormat sdf = new SimpleDateFormat(ANALYTICS_DATE_MASK);
         String now = sdf.format(new Date());
         ret = !now.equals(lastRunDate);
      }
      return ret;
   }

   @DefaultCommand
   public void setEnabled(@Option(name = "enabled", required = true) Boolean enabled)
   {
      configuration.setProperty(ANALYTICS_ENABLED, enabled);
      getAnalyticsTracker().setEnabled(enabled);
   }

   /**
    * Returns whether analytics is enabled or not
    *
    * @return
    */
   boolean isEnabled()
   {
      return configuration.getBoolean(ANALYTICS_ENABLED, Boolean.FALSE);
   }

   /**
    * Display info about the analytics usage
    */
   @Command("info")
   public void displayInfo()
   {
      shell.print("Analytics is: ");
      shell.println(ShellColor.BOLD, isEnabled() ? "enabled" : "disabled");
      Date lastRunDate = getLastRunDate();
      if (lastRunDate != null)
      {
         DateFormat dateInstance = DateFormat.getDateInstance(DateFormat.LONG);
         shell.print("Analytics was last run at: ");
         shell.println(ShellColor.BOLD, dateInstance.format(lastRunDate));
      }
      shell.print("Forge Version: ");
      shell.println(ShellColor.BOLD, getForgeVersion());

      shell.print("Operating System: ");
      shell.println(ShellColor.BOLD, getOS());

      shell.print("Java Version: ");
      shell.println(ShellColor.BOLD, getJavaVersion());

      shell.print("Installed Plugins: ");
      shell.println(ShellColor.BOLD, getInstalledPlugins().toString());
   }

   private Date getLastRunDate()
   {
      Date date = null;
      String lastRunDate = configuration.getString(ANALYTICS_LAST_RUN, null);
      if (lastRunDate != null)
      {
         SimpleDateFormat sdf = new SimpleDateFormat(ANALYTICS_DATE_MASK);
         try
         {
            return sdf.parse(lastRunDate);
         }
         catch (ParseException e)
         {
            return null;
         }
      }
      return date;
   }

   private void setLastRunDate(Date date)
   {
      SimpleDateFormat sdf = new SimpleDateFormat(ANALYTICS_DATE_MASK);
      configuration.setProperty(ANALYTICS_LAST_RUN, sdf.format(date));
   }

   private JGoogleAnalyticsTracker getAnalyticsTracker()
   {
      if (analyticsTracker == null)
      {
         AnalyticsConfigData config = new AnalyticsConfigData(FORGE_GOOGLE_ANALYTICS_KEY);
         config.setFlashVersion(getJavaVersion());
         analyticsTracker = new JGoogleAnalyticsTracker(config, GoogleAnalyticsVersion.V_4_7_2);
      }
      return analyticsTracker;
   }

   /**
    * Sends the current data to Google Analytics
    */
   private void sendInfoToAnalytics()
   {
      getAnalyticsTracker().trackPageView(getOS(), getForgeVersion(), "");
      for (String plugin : getInstalledPlugins())
      {
         getAnalyticsTracker().trackEvent("plugin", plugin);
      }
      setLastRunDate(new Date());
   }

   private String getOS()
   {
      if (OSUtils.isLinux())
      {
         return LinuxSystem.INSTANCE.getDistroNameAndVersion();
      }
      return OSUtils.getOsName();
   }

   private String getForgeVersion()
   {
      return environment.getRuntimeVersion();
   }

   private String getJavaVersion()
   {
      return System.getProperty("java.version");
   }

   private List<String> getInstalledPlugins()
   {
      List<String> plugins = new ArrayList<String>();
      Resource<?> registry = environment.getPluginDirectory().getChild("installed.xml");
      InputStream is = null;
      if (registry.exists())
      {
         try
         {
            is = registry.getResourceInputStream();
            for (Node plugin : XMLParser.parse(is).get("plugin"))
            {
               plugins.add(plugin.getAttribute("name"));
            }
         }
         finally
         {
            Streams.closeQuietly(is);
         }
      }
      return plugins;
   }
}
