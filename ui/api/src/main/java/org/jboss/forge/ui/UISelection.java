package org.jboss.forge.ui;

/**
 * Represents the objects with on which the {@link UIContext} is currently focused. This may be the current working
 * directory, highlighted files, text, or other focusable items.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * @param <SELECTIONTYPE> The selection type, must be common between all selections (if multiple).
 */
public interface UISelection<SELECTIONTYPE> extends Iterable<SELECTIONTYPE>
{
   // TODO define interface
}
