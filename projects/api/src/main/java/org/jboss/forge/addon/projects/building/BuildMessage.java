/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.building;

/**
 * A build message
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface BuildMessage
{
   /**
    * The message itself
    */
   String getMessage();

   /**
    * The severity of this message
    */
   Severity getSeverity();

   enum Severity
   {
      ERROR, INFO, WARN;
   }
}
