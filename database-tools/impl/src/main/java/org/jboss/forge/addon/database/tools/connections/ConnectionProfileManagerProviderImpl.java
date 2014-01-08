package org.jboss.forge.addon.database.tools.connections;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManager;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManagerProvider;

@Singleton
public class ConnectionProfileManagerProviderImpl implements
		ConnectionProfileManagerProvider {
	
	@Inject
	private ConnectionProfileManager defaultManager;
	
	private ConnectionProfileManager connectionProfileManager;

	@Override
	public void setConnectionProfileManager(ConnectionProfileManager manager) {
		this.connectionProfileManager = manager;
	}

	@Override
	public ConnectionProfileManager getConnectionProfileManager() {
		if (connectionProfileManager != null) {
			return connectionProfileManager;
		} else {
			return defaultManager;
		}
	}

}
