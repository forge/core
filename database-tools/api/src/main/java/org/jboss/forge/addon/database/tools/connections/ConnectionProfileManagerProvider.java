package org.jboss.forge.addon.database.tools.connections;

public interface ConnectionProfileManagerProvider {
	
	public void setConnectionProfileManager(ConnectionProfileManager manager);
	
	public ConnectionProfileManager getConnectionProfileManager();

}
