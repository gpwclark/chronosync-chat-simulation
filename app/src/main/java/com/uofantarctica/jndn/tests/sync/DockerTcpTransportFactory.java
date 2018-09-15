package com.uofantarctica.jndn.tests.sync;

import net.named_data.jndn.transport.Transport;

public class DockerTcpTransportFactory extends TcpTransportFactory {
	@Override
	public Transport getTransport() {
		return new DockerTcpTransport();
	}
}
