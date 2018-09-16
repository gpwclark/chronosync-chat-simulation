package com.uofantarctica.jndn.helpers;

import net.named_data.jndn.transport.TcpTransport;
import net.named_data.jndn.transport.Transport;

public class TcpTransportFactory implements TransportFactory {
	private final String host;
	private final int port;

	public TcpTransportFactory(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public Transport getTransport() {
		return new TcpTransport();
	}

	@Override
	public Transport.ConnectionInfo getConnectionInfo() {
		return new TcpTransport.ConnectionInfo(host, port);
	}
}
