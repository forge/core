package org.jboss.forge.scaffoldx;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import javax.inject.Qualifier;

/**
 * The {@link Qualifier} to be used by Scaffold-x providers to avoid 'ambiguous dependencies' error. The scaffold-x API
 * uses this qualifier when it is unknown whether ambiguous dependencies exist during the invocation of the API methods.
 * 
 * @author Vineet Reynolds
 * 
 */
@Documented
@Retention(RUNTIME)
@Qualifier
public @interface ScaffoldQualifier
{

}
