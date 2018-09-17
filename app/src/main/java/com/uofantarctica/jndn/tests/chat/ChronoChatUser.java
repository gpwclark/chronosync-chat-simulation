package com.uofantarctica.jndn.tests.chat;

import com.uofantarctica.jndn.helpers.FaceSecurity;
import com.uofantarctica.jndn.tests.sync.SyncQueue;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ChronoChatUser implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(ChronoChatUser.class);

	protected static ArrayList<String> generatedMessages = null;

	protected String screenName;
	protected String broadcastBaseName;
	protected String baseScreenName;
	protected String chatRoom;
	protected String hubPrefix;
	protected Face face;
	protected KeyChain keyChain;
	protected int participants;
	protected int participantNo;
	protected Name certificateName;
	protected SyncQueue queue;
	protected TestChat chatter;
	protected int[] messagesSentCountPerUser;
	protected int numMessages;
	protected List<Interest> interestsExpressed;

	public ChronoChatUser(int participantNo, int participants, String broadcastBaseName,
	                      String baseScreenName, String chatRoom, String hubPrefix,
	                      Face face, FaceSecurity.SecurityData securityData,
	                      SyncQueue queue, int[] messagesSentCountPerUser,
	                      int numMessages, List<Interest> interestExpressed) {
		this.participantNo = participantNo;
		this.participants = participants;
		this.baseScreenName = baseScreenName;
		this.broadcastBaseName = broadcastBaseName;
		this.screenName = generateScreenName(baseScreenName, participantNo);
		this.chatRoom = chatRoom;
		this.hubPrefix = hubPrefix;
		this.face = face;
		this.keyChain = securityData.keyChain;
		this.queue = queue;
		this.certificateName = securityData.certificateName;
		this.messagesSentCountPerUser = messagesSentCountPerUser;
		this.numMessages = numMessages;
		this.interestsExpressed = interestExpressed;
	}

	public static ArrayList<String> getMessages(int numMessages) {
		if (generatedMessages == null) {
			String sampleText = "sample_text";
			ArrayList<String> messages = new ArrayList<>();
			for (int i = 0; i < numMessages; ++i) {
				messages.add(sampleText + i);
			}
			generatedMessages = messages;
		}
		return generatedMessages;
	}

	public static void leave(Chat chat) {
		// The user entered the command to leave.
		chat.leave();
		chat.pumpFaceAwhile(1000);
	}

	public static void pumpFaceAwhile(Face face, long awhile) {
		Chatter.pumpFaceAwhile(face, awhile);
	}

	@Override
	public void run() {
		try {
			String testType = System.getProperty("runMock");
			if (testType == null || !testType.equals("true")) {
				log.debug( "RUNNING REAL CHAT TEST.");
				this.chatter = new TestChatChatter(screenName, broadcastBaseName, chatRoom,
					new Name(hubPrefix), face, keyChain, certificateName, interestsExpressed);
			}
			else {
				log.debug( "RUNNING MOCK CHAT TEST.");
				this.chatter = new MockTestChatChatter(screenName, broadcastBaseName, chatRoom,
					new Name(hubPrefix), face, keyChain, certificateName, interestsExpressed);
			}

			chatter.setTestContext(this, numMessages, participantNo,
				participants, baseScreenName);
			chatter.pumpFaceAwhile(5000);

			//create thread pool to
			//1. create chats and send series of predefined messages n times then send leave command.
			//2. Verify that each message was received n times from each thread.
			//3. each thread print metric: for each chatter participant number received over total.
			int totalMessagesSent = 0;
			ArrayList<String> messages = getMessages(numMessages);

			for (String m : messages) {
				long chatDelayTime = chatter.getChatDelayTime();
				long startTime = System.currentTimeMillis();
				long timeNow = System.currentTimeMillis();

				boolean sentMessage = false;
				while (!sentMessage) {
					if  ((timeNow - startTime) >= chatDelayTime) {
						chatter.sendMessage(m);
						++totalMessagesSent;
						//messagesSentCountPerUser is shared in
						// every thread but each thread only writes to
						// one cell.
						messagesSentCountPerUser[participantNo] =  totalMessagesSent;
						sentMessage = true;
					}
					chatter.pumpFaceAwhile(10);
					timeNow = System.currentTimeMillis();
				}
			}

			int numMessagesEachUserMustSend = numMessages;
			while(allUsersHaveNotSentAllMessages(numMessagesEachUserMustSend)) {
				chatter.pumpFaceAwhile(3000);
			}
			chatter.pumpFaceAwhile(15000); // for 3 sync lifetimes just to be sure.
			leave(chatter);
			chatter.submitStats(queue, numMessages);

			if (allUsersHaveNotSentAllMessages(numMessagesEachUserMustSend)) {
				log.error( " Failed to conduct valid experiment. " +
							"Not all messages were sent in chatter room, " +
							"results invalid.");
				System.exit(1);
			}

		} catch (Exception e) {
			log.error( "failed running chronochatuser thread", e);
		}
	}

	private boolean allUsersHaveNotSentAllMessages(int numMessagesEachUserMustSend) {
		boolean allUsersHaveNotSentAllMessages = false;
		for (int a : messagesSentCountPerUser) {
			if (a != numMessagesEachUserMustSend) {
				return true;
			}

		}
		return allUsersHaveNotSentAllMessages;
	}

	public static String generateScreenName(String screenName, int i) {
		int len = screenName.length();
		int half = len / 2;
		return i + screenName.substring(0, half) + i + screenName.substring(half) + i;
	}
}
