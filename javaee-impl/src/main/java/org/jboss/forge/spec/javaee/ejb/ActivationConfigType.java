package org.jboss.forge.spec.javaee.ejb;

public enum ActivationConfigType {

	// @MessageDriven(name = "AddressFileParserMDB", activationConfig = {
				// @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
				// @ActivationConfigProperty(propertyName = "destination", propertyValue = AppConstants.ADDRESS_FILE_RESOLVER_Q),
				// @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
				// @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1"),
				// @ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "10800"),
				// @ActivationConfigProperty(propertyName = "dLQMaxResent", propertyValue = "0") 
	//})
		activationConfig("javax.ejb.ActivationConfigProperty"),
		destinationType("destinationType"),
		destination("destination"),
		acknowledgeMode( "acknowledgeMode"),
		maxSession("maxSession"),
		transactionTimeout("transactionTimeout"),
		dLQMaxResent("dLQMaxResent");

			private String propertyName;

			private ActivationConfigType(String propertyName) {
				this.propertyName = propertyName;
			}

			public String getPropertyName() {
				return propertyName;
			}
		}
