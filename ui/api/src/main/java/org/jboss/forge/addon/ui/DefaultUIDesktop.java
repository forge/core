/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Default implementation of {@link UIDesktop}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class DefaultUIDesktop implements UIDesktop
{
   @Override
   public void open(File file) throws IOException
   {
      getDesktop().open(file);
   }

   @Override
   public void edit(File file) throws IOException
   {
      getDesktop().edit(file);
   }

   @Override
   public void print(File file) throws IOException
   {
      getDesktop().print(file);
   }

   @Override
   public void browse(URI uri) throws IOException
   {
      getDesktop().browse(uri);
   }

   @Override
   public void mail() throws IOException
   {
      getDesktop().mail();
   }

   @Override
   public void mail(URI mailtoURI) throws IOException
   {
      getDesktop().mail(mailtoURI);
   }

   protected Desktop getDesktop()
   {
      return Desktop.getDesktop();
   }

}
