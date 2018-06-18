package com.uofantarctica.jndn_chat_sim;

public interface ChronoChatTest extends Chat {
	void setTestContext(ChronoChatUser cu, int numMessages, int
		participantNo, int participants, String baseScreenName);
	void submitStats(SyncQueue queue, int numMessages);
	long getChatDelayTime();
}
