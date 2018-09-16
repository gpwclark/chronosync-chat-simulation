package com.uofantarctica.jndn.tests.chat;

public final class ChatSimulationBuilder {
	int participants;
	int numMessages;
	String screenName;
	String hubPrefix;
	String chatRoom;
	String broadcastBaseName;

	private ChatSimulationBuilder() {
	}

	public static ChatSimulationBuilder aChatSimulation() {
		return new ChatSimulationBuilder();
	}

	public ChatSimulationBuilder withNumParticipants(int participants) {
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

	public ChatSimulationBuilder withChatRoom(String chatRoom) {
		this.chatRoom = chatRoom;
		return this;
	}

	public ChatSimulationBuilder withBroadcastBaseName(String broadcastBaseName) {
		this.broadcastBaseName = broadcastBaseName;
		return this;
	}

	public ChatSimulation build() {
		return new ChatSimulation(participants, numMessages, broadcastBaseName, screenName, hubPrefix, chatRoom);
	}
}
