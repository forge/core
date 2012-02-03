package org.jboss.forge.resources.java;

/**
 * @author Rudy De Busscher
 *         Called for each Java file which is found in the project.
 */
public interface JavaResourceVisitor
{
    /**
     * Called when a Java File is found.
     *
     * @param javaResource The JavaResource for the found file.
     */
    void visit(final JavaResource javaResource);
}
