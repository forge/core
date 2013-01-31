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
   /**
    * Returns the first element in this selection
    *
    * @return
    */
   public SELECTIONTYPE get();

   /**
    * Returns the number of elements of this selection
    *
    * @return
    */
   public int size();

}
