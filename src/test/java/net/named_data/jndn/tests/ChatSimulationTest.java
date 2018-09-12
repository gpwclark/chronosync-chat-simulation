package net.named_data.jndn.tests;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChatSimulationTest {

	final static String NFD_SERVICE = "NFD_SERVICE";
	final int INTERNAL_PORT = 6363;
	String host;
	int port;

	@ClassRule
	public static DockerComposeRule docker = DockerComposeRule.builder()
			.file("src/test/resources/docker-compose.yml")
			.waitingForService(NFD_SERVICE, HealthChecks.toHaveAllPortsOpen())
			.build();

	@Before
	public void setUp() throws Exception {
		DockerPort nfd = docker.containers()
				.container(NFD_SERVICE)
				.port(INTERNAL_PORT);
		host = nfd.getIp();
		port = nfd.getExternalPort();
	}

	@After
	public void tearDown() throws Exception {
		docker.containers().container(NFD_SERVICE).stop();
	}

	@Test
	public void correctNumberOfMessageLogs() {
	}

	@Test
	public void correctNumberOfChats() {
	}

	@Test
	public void correctNumberOfMesages() {
	}
}