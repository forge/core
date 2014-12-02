/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.jboss.forge.addon.ui.UIDesktop;

/**
 * Default implementation of {@link UIDesktop}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class DefaultUIDesktop implements UIDesktop
{
   private final Desktop desktop;

   public DefaultUIDesktop()
   {
      this.desktop = Desktop.getDesktop();
   }

   public DefaultUIDesktop(Desktop desktop)
   {
      this.desktop = desktop;
   }

   @Override
   public void open(File file) throws IOException
   {
      desktop.open(file);
   }

   @Override
   public void edit(File file) throws IOException
   {
      desktop.edit(file);
   }

   @Override
   public void print(File file) throws IOException
   {
      desktop.print(file);
   }

   @Override
   public void browse(URI uri) throws IOException
   {
      desktop.browse(uri);
   }

   @Override
   public void mail() throws IOException
   {
      desktop.mail();
   }

   @Override
   public void mail(URI mailtoURI) throws IOException
   {
      desktop.mail(mailtoURI);
   }
}
