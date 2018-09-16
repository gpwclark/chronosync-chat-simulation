package com.uofantarctica.jndn.helpers;

import net.named_data.jndn.transport.TcpTransport;
import net.named_data.jndn.transport.Transport;

import java.io.IOException;

class DockerTcpTransport extends TcpTransport {
	public boolean isLocal(Transport.ConnectionInfo connectionInfo) throws IOException {
		return false;
	}
}


