package org.jboss.forge.project.facets;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.Facet;

/**
 * A facet supporting queries for non-editable JavaSource in a project's dependencies.
 * @author jfraney
 *
 */
public interface JavaBinaryFacet extends Facet {
	
	/**
	 * Returns a non-editable {@link JavaSource}
	 * (one of {@link JavaInterface}, {@link JavaEnum} or {@link JavaClass})
	 * with the given fully qualified name.
	 * @param name is the fully qualified type name to resolve.
	 * @return the java type as a {@link JavaSource} or null if not found.
	 */
	JavaSource<? extends JavaSource<?>> find(String qualifiedName);
	
	/**
	 * Returns a non-editable {@link JavaSource}
	 * (one of {@link JavaInterface}, {@link JavaEnum} or {@link JavaClass})
	 *  with the given simple name in the
	 * given package.
	 * @param pkg is a valid package name as per import statement, and so may contain
	 * the '*' metacharacter.
	 * @param simpleName is the last component of a fully qualified java type name
	 * @return the java type as a {@link JavaSource} or null if not found.
	 */
	JavaSource<? extends JavaSource<?>> find(String pkg, String simpleName);
	
	/**
	 * Returns a non-editable {@link JavaSource} 
	 * (one of {@link JavaInterface}, {@link JavaEnum} or {@link JavaClass})
	 * with the given simple name found within the ordered list
	 * of packages.
	 * @param pkgs is an ordered list of valid package names as per import statement,
	 *  and so may contain the '*' metacharacter.
	 * @param simpleName
	 * @return the java type as a {@link JavaSource} or null if not found.
	 */
	JavaSource<? extends JavaSource<?>> find(String[] pkgs, String simpleName);


}
