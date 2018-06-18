package com.uofantarctica.jndn_chat_sim;

import net.named_data.jndn.OnData;
import net.named_data.jndn.sync.ChronoSync2013;

public interface SyncAdapter {
	void publishNextMessage(long seqNo, String messageType, String message, double time);
	long getProducerSequenceNo(String prefix_, long sessionNo_);
	void publishNextSequenceNo();
	long getSequenceNo();
	long getSessionNo();
	void initSyncForDataSet(OnData onData, ChronoSync2013.OnInitialized onInitialized, String chatRoom, String screenName);
}
