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
class BuildMessageImpl implements BuildMessage
{
   private String message;
   private Severity severity;

   public BuildMessageImpl(Severity severity, String message)
   {
      this.severity = severity;
      this.message = message;
   }

   public String getMessage()
   {
      return message;
   }

   public Severity getSeverity()
   {
      return severity;
   }
}
