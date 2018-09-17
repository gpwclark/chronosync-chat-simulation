package com.uofantarctica.jndn.tests;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import com.uofantarctica.jndn.helpers.DockerTcpTransportFactory;
import com.uofantarctica.jndn.helpers.TransportConfiguration;
import com.uofantarctica.jndn.helpers.TransportFactory;
import com.uofantarctica.jndn.tests.chat.ChatSimulation;
import com.uofantarctica.jndn.tests.chat.ChatSimulationBuilder;
import com.uofantarctica.jndn.tests.chat.UserChatSummary;
import net.named_data.jndn.encoding.WireFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChatSimulationTest {

	final static String NFD_SERVICE = "nfd_service";
	final int INTERNAL_PORT = 6363;
	String host;
	int port;
	int numMessages = 10;
	int numParticipants = 2;
	UserChatSummary summary = null;

	@ClassRule
	public static DockerComposeRule docker = DockerComposeRule.builder()
			.file("src/test/resources/docker-compose.yml")
			.waitingForService(NFD_SERVICE, HealthChecks.toHaveAllPortsOpen())
			.build();

	@Before
	public void setUp() throws Exception {
		WireFormat.getDefaultWireFormat();
		DockerPort nfd = docker.containers()
				.container(NFD_SERVICE)
				.port(INTERNAL_PORT);
		host = nfd.getIp();
		port = nfd.getExternalPort();
		TransportFactory transportFactory = new DockerTcpTransportFactory(host, port);
		TransportConfiguration.setTransportFactory(transportFactory);

		String screenName = "scratchy";
		String hubPrefix = "/ndn/broadcast/chat-room";
		String chatRoom = "ndnchat";
		String broadcastBaseName = "/ndn/broadcast/sync-simulation-test";

		ChatSimulationBuilder builder = ChatSimulationBuilder.aChatSimulation();
		builder.withScreenName(screenName)
				.withBroadcastBaseName(broadcastBaseName)
				.withHubPrefix(hubPrefix)
				.withChatRoom(chatRoom)
				.withNumMessages(numMessages)
				.withNumParticipants(numParticipants);
		ChatSimulation simulation = builder.build();
		summary = simulation.simulate();
	}

	@After
	public void tearDown() throws Exception {
		docker.containers().container(NFD_SERVICE).stop();
	}

	@Test
	public void chatSimulationTest() {
		assertEquals("Need one message log per participant", numParticipants, summary.getAccumulationCount());
		assertEquals("Need a certain number of unique chats, or unique combinations of chatters",
				UserChatSummary.getExpectedNumUniqueChats(numParticipants), summary.getNumUniqueChats());
		assertEquals("Need a certain number of total messages received.",
				UserChatSummary.getExpectedTotalCount(numParticipants, numMessages),
				summary.getTotalCount());
	}
}