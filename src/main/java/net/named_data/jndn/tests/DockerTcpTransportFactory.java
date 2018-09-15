package net.named_data.jndn.tests;

import net.named_data.jndn.transport.Transport;

public class DockerTcpTransportFactory extends TcpTransportFactory {
	@Override
	public Transport getTransport() {
		return new DockerTcpTransport();
	}
}
