/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Interacts with the desktop bound to the {@link UIProvider}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface UIDesktop
{
   /**
    * Launches the associated application to open the file.
    *
    * <p>
    * If the specified file is a directory, the file manager of the current platform is launched to open it.
    *
    * @param file the file to be opened with the associated application
    * @throws IOException if the specified file has no associated application or the associated application fails to be
    *            launched
    */
   void open(File file) throws IOException;

   /**
    * Launches the associated editor application and opens a file for editing.
    *
    * @param file the file to be opened for editing
    * @throws IOException if the specified file has no associated editor, or the associated application fails to be
    *            launched
    */
   void edit(File file) throws IOException;

   /**
    * Prints a file with the native desktop printing facility, using the associated application's print command.
    *
    * @param file the file to be printed
    * @throws IOException if the specified file has no associated application that can be used to print it
    */
   void print(File file) throws IOException;

   /**
    * Launches the default browser to display a {@code URI}. If the default browser is not able to handle the specified
    * {@code URI}, the application registered for handling {@code URIs} of the specified type is invoked. The
    * application is determined from the protocol and path of the {@code URI}, as defined by the {@code URI} class.
    * <p>
    * 
    * @param uri the URI to be displayed in the user default browser
    * @throws IOException if the user default browser is not found, or it fails to be launched, or the default handler
    *            application failed to be launched
    * @see java.net.URI
    * @see java.awt.AWTPermission
    * @see java.applet.AppletContext
    */
   void browse(URI uri) throws IOException;

   /**
    * Launches the mail composing window of the user default mail client.
    *
    * @throws IOException if the user default mail client is not found, or it fails to be launched
    */
   void mail() throws IOException;

   /**
    * Launches the mail composing window of the user default mail client, filling the message fields specified by a
    * {@code mailto:} URI.
    *
    * <p>
    * A <code>mailto:</code> URI can specify message fields including <i>"to"</i>, <i>"cc"</i>, <i>"subject"</i>,
    * <i>"body"</i>, etc. See <a href="http://www.ietf.org/rfc/rfc2368.txt">The mailto URL scheme (RFC 2368)</a> for the
    * {@code mailto:} URI specification details.
    *
    * @param mailtoURI the specified {@code mailto:} URI
    * @throws IOException if the user default mail client is not found or fails to be launched
    */
   void mail(URI mailtoURI) throws IOException;
}
