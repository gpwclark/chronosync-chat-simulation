/**
 * Copyright (C) 2014-2018 Regents of the University of California.
 * @author: Jeff Thompson <jefft0@remap.ucla.edu>
 * Derived from ChronoChat-js by Qiuhan Ding and Wentao Shang.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * A copy of the GNU Lesser General Public License is in the file COPYING.
 */

package net.named_data.jndn.tests;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SafeBag;
import net.named_data.jndn.transport.TcpTransport;
import net.named_data.jndn.util.Blob;

import static net.named_data.jndn.tests.ChatSimulation.getSecurityData;

// Define the ChronoChat class here so that the ChronoChat demo is self-contained.

public class TestChronoChat {
	private static final Logger log = Logger.getLogger(TestChronoChat.class.getName());

	public static void
	main(String[] args) {
		final int participants = 2;//Integer.parseInt(participantInt);
		final int numMessages = 100;//Integer.parseInt(numMessagesInt);
		String screenName = "scratchy";
		String hubPrefix = "ndn/broadcast/edu/ucla/remap";
		String defaultChatRoom = "ndnchat";
		String chatRoom = defaultChatRoom;
		String host = "127.0.0.1";
		int port = 6363;

		ChatSimulationBuilder builder = ChatSimulationBuilder.aChatSimulation();
		builder.withParticipants(2)
				.withNumMessages(100)
				.withScreenName(screenName)
				.withHubPrefix(hubPrefix)
				.withDefaultChatRoom(defaultChatRoom)
				.withHost(host)
				.withPort(port);

		ChatSimulation simulation = builder.build();
		boolean success = simulation.simulate();
		if (success) {
			System.out.println("success");
		}
		else {
			System.out.println("failure");
		}
	}

}
