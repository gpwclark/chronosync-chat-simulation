package net.named_data.jndn.tests;

public final class ChatSimulationBuilder {
	int participants;
	int numMessages;
	String screenName;
	String hubPrefix;
	String chatRoom;
	String host;
	int port;
	TransportFactory transportFactory;

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

	public ChatSimulationBuilder withHost(String host) {
		this.host = host;
		return this;
	}

	public ChatSimulationBuilder withPort(int port) {
		this.port = port;
		return this;
	}


	public ChatSimulationBuilder withTransportFactory(TransportFactory transportFactory) {
		this.transportFactory = transportFactory;
		return this;
	}
	public ChatSimulation build() {
		return new ChatSimulation(participants, numMessages, screenName, hubPrefix, chatRoom, host, port, transportFactory);
	}
}
