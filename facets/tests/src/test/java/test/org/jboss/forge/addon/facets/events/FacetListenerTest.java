/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.addon.facets.events;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.FacetListener;
import org.jboss.forge.addon.facets.events.FacetEvent;
import org.jboss.forge.addon.facets.events.FacetInstalled;
import org.jboss.forge.addon.facets.events.FacetRegistered;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the {@link FacetInstalled} behavior
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class FacetListenerTest
{
   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addClasses(MockFacet.class,
                        MockFaceted.class, MockAlreadyInstalledFacet.class);
      return archive;
   }

   @Inject
   private FacetFactory facetFactory;

   @Test
   public void testFacetInstalled()
   {
      final List<FacetEvent> facetEvents = new ArrayList<>();
      ListenerRegistration<FacetListener> registration = facetFactory
               .addFacetListener(new FacetListener()
               {

                  @Override
                  public void processEvent(FacetEvent event)
                  {
                     facetEvents.add(event);
                  }
               });
      try
      {
         MockFaceted faceted = new MockFaceted();
         MockFacet facet = facetFactory.install(faceted, MockFacet.class);
         Assert.assertNotNull(facet);
         Assert.assertEquals(1, facetEvents.size());
         FacetEvent event = facetEvents.get(0);
         Assert.assertThat(event, instanceOf(FacetInstalled.class));
         Assert.assertSame(facet, event.getFacet());
      }
      finally
      {
         registration.removeListener();
      }
   }

   @Test
   public void testFacetAlreadyInstalledShouldFireRegisterEvents()
   {
      final List<FacetEvent> facetEvents = new ArrayList<>();
      ListenerRegistration<FacetListener> registration = facetFactory
               .addFacetListener(new FacetListener()
               {

                  @Override
                  public void processEvent(FacetEvent event)
                  {
                     facetEvents.add(event);
                  }
               });
      try
      {
         MockFaceted faceted = new MockFaceted();
         MockAlreadyInstalledFacet facet = facetFactory.install(faceted, MockAlreadyInstalledFacet.class);
         Assert.assertNotNull(facet);
         Assert.assertEquals(1, facetEvents.size());
         FacetEvent event = facetEvents.get(0);
         Assert.assertThat(event, instanceOf(FacetRegistered.class));
         Assert.assertSame(facet, event.getFacet());
      }
      finally
      {
         registration.removeListener();
      }
   }
}
