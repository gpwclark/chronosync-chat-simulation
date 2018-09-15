package com.uofantarctica.jndn.tests.sync;

import net.named_data.jndn.Data;

public interface SyncAdapter {
	void publishNextMessage(Data data);
	long getProducerSequenceNo(String prefix_, long sessionNo_);
	void publishNextSequenceNo();
	long getSequenceNo();
}
