package net.named_data.jndn.tests;

public interface SyncAdapter {
	void publishNextMessage(long seqNo, ChatbufProto.ChatMessage.ChatMessageType messageType, String message, double time);
	long getProducerSequenceNo(String prefix_, long sessionNo_);
	void publishNextSequenceNo();
	long getSequenceNo();
}
