/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies;

import java.util.List;
import java.util.ServiceLoader;

import org.jboss.forge.resources.DependencyResource;

/**
 * 
 * Used to resolve {@link Dependency} versions, {@link DependencyResource} artifacts, and dependencies of a given
 * {@link Dependency}
 * <p>
 * <b>Example usage:</b>
 * <p>
 * 
 * <pre>
 * &#064;Inject
 * DepenencyResolver resolver;
 * 
 * Dependency query =
 *          DependencyBuilder.create(&quot;com.example:example:[1.0],[2.0,)&quot;);
 * 
 * List&lt;Dependency&gt; versions = resolver.resolveVersions(query);
 * List&lt;DependencyResource&gt; artifacts = resolver.resolveArtifacts(query);
 * List&lt;DependencyResource&gt; dependencies = resolver.resolveDependencies(query);
 * </pre>
 * <p>
 * <b>Version query syntax is as follows:</b>
 * <table>
 * <tr>
 * <td>1.0</td>
 * <td>version == 1.0</td>
 * </tr>
 * <tr>
 * <td>[1.0,2.0)</td>
 * <td>1.0 &lt;= version &lt; 2.0</td>
 * </tr>
 * <tr>
 * <td>[1.0,2.0]</td>
 * <td>1.0 &lt;= version &lt;= 2.0</td>
 * </tr>
 * <tr>
 * <td>[1.5,)</td>
 * <td>1.5 &lt;= version</td>
 * </tr>
 * <tr>
 * <td>(,1.0],[1.2,)</td>
 * <td>version &lt;= 1.0, and version &gt;= 2.0</td>
 * </tr>
 * </table>
 * <p>
 * <b>Implementing additional {@link DependencyResolverProvider} classes</b>
 * 
 * {@link DependencyResolverProvider} defines the interface to be used when creating additional providers for
 * {@link DependencyResolver} using the {@link ServiceLoader} interface.
 * <p>
 * A service provider is identified by placing a provider-configuration file in the META-INF/services directory:
 * <p>
 * 
 * <literal> /META-INF/services/org.jboss.forge.project.dependencies.DependencyResolverProvider </literal>
 * 
 * <p>
 * The file contains a list of fully-qualified class names of concrete implementation classes, one per line. Space and
 * tab characters surrounding each name, as well as blank lines, are ignored.
 * <p>
 * <b>For example:</b>
 * <p>
 * 
 * <pre>
 * /META-INF/services/org.jboss.forge.project.dependencies.DependencyResolverProvider<br>
 * #this file contains a line for each implementation
 * com.example.MyCustomImplementation
 * </pre>
 * <p>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface DependencyResolverProvider
{
   /**
    * Resolve a set of {@link DependencyResource} artifacts matching the given query, searching in the default
    * repository.
    */
   List<DependencyResource> resolveArtifacts(Dependency query);

   /**
    * Resolve a set of {@link DependencyResource} artifacts matching the given query, searching in only the given
    * {@link DependencyRepository}.
    */
   List<DependencyResource> resolveArtifacts(Dependency query, DependencyRepository repository);

   /**
    * Resolve a set of {@link DependencyResource} artifacts matching the given query, searching in only the given list
    * of {@link DependencyRepository} instances.
    */
   List<DependencyResource> resolveArtifacts(final Dependency dep, final List<DependencyRepository> repositories);

   /**
    * Resolve a set of {@link DependencyResource} dependencies for the given query, searching in the default repository.
    * <p>
    * 
    * @return a list of {@link DependencyResource} dependencies on which the given query artifact depends.
    */
   List<DependencyResource> resolveDependencies(Dependency query);

   /**
    * Resolve a set of {@link DependencyResource} dependencies for the given query, searching in only the given
    * {@link DependencyRepository}.
    * <p>
    * 
    * @return a list of {@link DependencyResource} dependencies on which the given query artifact depends.
    */
   List<DependencyResource> resolveDependencies(Dependency query, DependencyRepository repository);

   /**
    * Resolve a set of {@link DependencyResource} dependencies for the given query, searching in only the given list of
    * {@link DependencyRepository} instances.
    * <p>
    * 
    * @return a list of {@link DependencyResource} dependencies on which the given query artifact depends.
    */
   List<DependencyResource> resolveDependencies(final Dependency dep, final List<DependencyRepository> repositories);

   /**
    * Resolve {@link DependencyMetadata} for a given {@link Dependency}, searching the default repository. This returns
    * information about the configured repositories, dependencies, and managed dependencies of the specified query.
    * <p>
    * Note: This method does not accept version ranges. A single version must be specified.
    * <p>
    * <b>Valid query version:</b> 1.0 <b><br>
    * Invalid query version:</b> [1.0,2.0]
    */
   DependencyMetadata resolveDependencyMetadata(Dependency query);

   /**
    * Resolve {@link DependencyMetadata} for a given {@link Dependency}, searching only the given
    * {@link DependencyRepository}. This returns information about the configured repositories, dependencies, and
    * managed dependencies of the specified query.
    * <p>
    * Note: This method does not accept version ranges. A single version must be specified.
    * <p>
    * <b>Valid query version:</b> 1.0 <b><br>
    * Invalid query version:</b> [1.0,2.0]
    */
   DependencyMetadata resolveDependencyMetadata(Dependency query, DependencyRepository repository);

   /**
    * Resolve {@link DependencyMetadata} for a given {@link Dependency}, searching only the given
    * {@link DependencyRepository} instances. This returns information about the configured repositories, dependencies,
    * and managed dependencies of the specified query.
    * <p>
    * Note: This method does not accept version ranges. A single version must be specified.
    * <p>
    * <b>Valid query version:</b> 1.0 <b><br>
    * Invalid query version:</b> [1.0,2.0]
    */
   DependencyMetadata resolveDependencyMetadata(Dependency query, List<DependencyRepository> repositories);

   /**
    * Resolve a set of {@link Dependency} versions matching the given query, searching in the default repository.
    */
   List<Dependency> resolveVersions(Dependency query);

   /**
    * Resolve a set of {@link Dependency} versions matching the given query, searching in only the given
    * {@link DependencyRepository}.
    */
   List<Dependency> resolveVersions(Dependency query, DependencyRepository repository);

   /**
    * Resolve a set of {@link Dependency} versions matching the given query, searching in only the given list of
    * {@link DependencyRepository} instances.
    */
   List<Dependency> resolveVersions(final Dependency dep, final List<DependencyRepository> repositories);
}
