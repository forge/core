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
package org.metawidget.forge.rewrite;

import javax.servlet.ServletContext;

import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationBuilder;
import com.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class URLRewriteConfiguration extends HttpConfigurationProvider
{

   @Override
   public Configuration getConfiguration(ServletContext context)
   {

      return ConfigurationBuilder
               .begin()
      /*
                     // Rewrite
                     .addRule(TrailingSlash.remove())

                     // Application mappings
                     .addRule(Join.path("/").to("/faces/index.xhtml"))

                     .addRule(Join.path("/{domain}").where("domain").matches("[a-zA-Z$_0-9]+").to("/faces/scaffold/{domain}/list.xhtml"))
                     .addRule(Join.path("/{domain}/{id}").where("id").matches("\\d+")
                              .to("/faces/scaffold/{domain}/list.xhtml"))
                     .addRule(Join.path("/{domain}/create").to("/faces/scaffold/{domain}/create.xhtml"))

                     // 404 and Error
                     .addRule(Join.path("/404").to("/faces/404.html"))
                     .addRule(Join.path("/error").to("/faces/500.html"))

                     .defineRule().when(
                              Direction.isInbound()
                                       .and(DispatchType.isRequest())
                                       .and(Path.matches(".*\\.xhtml"))
                                       .andNot(Path.matches(".*javax\\.faces\\.resource.*"))
                                       .andNot(Path.matches("/rfRes/.*")))
                     .perform(Forward.to("/404"))
                     */
      ;
   }

   @Override
   public int priority()
   {
      return 10000;
   }

}
