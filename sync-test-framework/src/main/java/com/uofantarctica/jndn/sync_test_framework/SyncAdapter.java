package com.uofantarctica.jndn.sync_test_framework;

import net.named_data.jndn.Data;

public interface SyncAdapter {
	void publishNextMessage(Data data);
	long getProducerSequenceNo(String prefix_, long sessionNo_);
	void publishNextSequenceNo();
	long getSequenceNo();
}
