package net.named_data.jndn.tests;

public final class ChatSimulationBuilder {
	/*
		final int participants = 2;//Integer.parseInt(participantInt);
		final int numMessages = 100;//Integer.parseInt(numMessagesInt);
		final String screenName = "scratchy";
		final String hubPrefix = "ndn/broadcast/edu/ucla/remap";
		final String defaultChatRoom = "ndnchat";
		final String chatRoom = defaultChatRoom;
		final String host = "127.0.0.1";
		final int port = 6363;
		SyncQueue<ArrayList<UserChatSummary>> resultQueue = new SyncQueue<>(5);
		*/
	int participants;
	int numMessages;
	String screenName;
	String hubPrefix;
	String defaultChatRoom;
	String chatRoom;
	String host;
	int port;

	private ChatSimulationBuilder() {
	}

	public static ChatSimulationBuilder aChatSimulation() {
		return new ChatSimulationBuilder();
	}

	public ChatSimulationBuilder withParticipants(int participants) {
		this.participants = participants;
		return this;
	}

	public ChatSimulationBuilder withNumMessages(int numMessages) {
		this.numMessages = numMessages;
		return this;
	}

	public ChatSimulationBuilder withScreenName(String screenName) {
		this.screenName = screenName;
		return this;
	}

	public ChatSimulationBuilder withHubPrefix(String hubPrefix) {
		this.hubPrefix = hubPrefix;
		return this;
	}

	public ChatSimulationBuilder withDefaultChatRoom(String defaultChatRoom) {
		this.defaultChatRoom = defaultChatRoom;
		return this;
	}

	public ChatSimulationBuilder withChatRoom(String chatRoom) {
		this.chatRoom = chatRoom;
		return this;
	}

	public ChatSimulationBuilder withHost(String host) {
		this.host = host;
		return this;
	}

	public ChatSimulationBuilder withPort(int port) {
		this.port = port;
		return this;
	}

	public ChatSimulation build() {
		return new ChatSimulation(participants, numMessages, screenName, hubPrefix, defaultChatRoom, chatRoom, host, port);
	}
}
