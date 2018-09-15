package net.named_data.jndn.tests;

import net.named_data.jndn.transport.Transport;

public interface TransportFactory {
	Transport getTransport();
	Transport.ConnectionInfo getConnectionInfo(String host, int port);
}
