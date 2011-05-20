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
package org.jboss.forge.spec.javaee.validation.completer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;

import static org.jboss.forge.spec.javaee.validation.util.ResourceHelper.getJavaClassFromResource;

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
    public List<Object> getCompletionTokens()
    {
        final List<Object> tokens = new ArrayList<Object>();
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
