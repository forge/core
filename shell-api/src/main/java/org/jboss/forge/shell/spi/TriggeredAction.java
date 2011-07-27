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
package org.jboss.forge.shell.spi;

import java.awt.event.ActionListener;

/**
 * Allows the shell to register an ActionListener that is performed when the corresponding character is being
 * read by the ConsoleReader.
 * 
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 * 
 */
public interface TriggeredAction
{
   /**
    * The char that is used to register the ActionListener as a TriggeredAction in the Shell.
    */
   public char getTrigger();
   
   /**
    * The ActionListener for which the actionPerformed method will be invoked when the trigger character is read.
    */
   public ActionListener getListener();
}
