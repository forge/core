/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell;

import java.io.OutputStream;
import java.util.List;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface ShellHistory
{
   void writeToHistory(String command);

   void setHistoryOutputStream(OutputStream stream);

   void setHistory(List<String> lines);
}
