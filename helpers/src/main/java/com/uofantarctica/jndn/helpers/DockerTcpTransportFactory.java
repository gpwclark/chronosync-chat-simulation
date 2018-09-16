package com.uofantarctica.jndn.helpers;

import net.named_data.jndn.transport.Transport;

public class DockerTcpTransportFactory extends TcpTransportFactory {
	public DockerTcpTransportFactory(String host, int port) {
		super(host, port);
	}

	@Override
	public Transport getTransport() {
		return new DockerTcpTransport();
	}
}
