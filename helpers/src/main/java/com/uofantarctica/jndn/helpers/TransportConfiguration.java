package com.uofantarctica.jndn.helpers;

import net.named_data.jndn.Face;

public class TransportConfiguration {
	private static TransportFactory transportFactory;

	public static void setTransportFactory(TransportFactory newTransportFactory) {
		transportFactory = newTransportFactory;
	}

	public static Face getFace() {
		return new Face(transportFactory.getTransport(), transportFactory.getConnectionInfo());
	}
}
