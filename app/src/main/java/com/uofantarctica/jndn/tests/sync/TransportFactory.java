package com.uofantarctica.jndn.tests.sync;

import net.named_data.jndn.transport.Transport;

public interface TransportFactory {
	Transport getTransport();
	Transport.ConnectionInfo getConnectionInfo(String host, int port);
}
