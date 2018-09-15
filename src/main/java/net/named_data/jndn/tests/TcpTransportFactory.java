package net.named_data.jndn.tests;

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
