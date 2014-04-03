/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.git.gitignore;

import java.util.LinkedList;
import java.util.List;

public class GitIgnoreTemplateGroup
{

   private final String name;
   private final List<String> templates;

   public GitIgnoreTemplateGroup(String name)
   {
      this(name, new LinkedList<String>());
      
   }
   
   public GitIgnoreTemplateGroup(String name, List<String> templates)
   {
      this.name = name;
      this.templates = templates;
   }
   
   public void add(String template)
   {
      templates.add(template);
   }

   public String getName()
   {
      return name;
   }

   public List<String> getTemplates()
   {
      return templates;
   }
   
}
