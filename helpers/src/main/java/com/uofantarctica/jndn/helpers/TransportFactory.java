package com.uofantarctica.jndn.helpers;

import net.named_data.jndn.Face;
import net.named_data.jndn.transport.Transport;

public interface TransportFactory {
	Transport getTransport();
	Transport.ConnectionInfo getConnectionInfo();
}
