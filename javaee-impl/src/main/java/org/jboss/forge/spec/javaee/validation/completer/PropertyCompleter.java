/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.validation.completer;

import static org.jboss.forge.spec.javaee.validation.util.ResourceHelper.getJavaClassFromResource;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;

/**
 * @author Kevin Pollet
 */
public class PropertyCompleter extends SimpleTokenCompleter
{
    private final Shell shell;

    @Inject
    public PropertyCompleter(Shell shell)
    {
        this.shell = shell;
    }

    @Override
    public List<String> getCompletionTokens()
    {
        final List<String> tokens = new ArrayList<String>();
        final Resource<?> currentResource = shell.getCurrentResource();

        try
        {

            final JavaClass javaClass = getJavaClassFromResource(currentResource);
            for (Field<JavaClass> oneField : javaClass.getFields())
            {
                tokens.add(oneField.getName());
            }

        } catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }

        return tokens;
    }
}
