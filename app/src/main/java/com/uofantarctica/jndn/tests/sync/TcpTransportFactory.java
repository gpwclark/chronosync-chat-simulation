package com.uofantarctica.jndn.tests.sync;

import net.named_data.jndn.transport.TcpTransport;
import net.named_data.jndn.transport.Transport;

public class TcpTransportFactory implements TransportFactory {
	@Override
	public Transport getTransport() {
		return new TcpTransport();
	}

	@Override
	public Transport.ConnectionInfo getConnectionInfo(String host, int port) {
		return new TcpTransport.ConnectionInfo(host, port);
	}
}
