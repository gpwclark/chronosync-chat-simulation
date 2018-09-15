package net.named_data.jndn.tests;

public interface TestChat extends Chat {
	void setTestContext(ChronoChatUser cu, int numMessages, int
		participantNo, int participants, String baseScreenName);
	void submitStats(SyncQueue queue, int numMessages);
	long getChatDelayTime();
}
