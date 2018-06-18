package com.uofantarctica.jndn_chat_sim;

import com.uofantarctica.dsync.model.SyncAdapter;
import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChronoChatUser implements Runnable {
	private static final Logger log = Logger.getLogger(MockChronoChatTester.class.getName());

	protected static ArrayList<String> generatedMessages = null;

	protected String screenName;
	protected String baseScreenName;
	protected String chatRoom;
	protected String hubPrefix;
	protected Face face;
	protected KeyChain keyChain;
	protected int participants;
	protected int participantNo;
	protected Name certificateName;
	protected SyncQueue queue;
	protected ChronoChatTest chat;
	protected int[] messagesSentCountPerUser;
	protected int numMessages;
	protected SyncAdapter sync;

	public ChronoChatUser(int participantNo, int participants, String baseScreenName,
												String chatRoom, String hubPrefix, Face face, KeyChain keyChain,
	                      SyncQueue queue, Name certificateName, int[] messagesSentCountPerUser,
	                      int numMessages, SyncAdapter sync) {
		this.participantNo = participantNo;
		this.participants = participants;
		this.baseScreenName = baseScreenName;
		this.screenName = generateScreenName(baseScreenName, participantNo);
		this.chatRoom = chatRoom;
		this.hubPrefix = hubPrefix;
		this.face = face;
		this.keyChain = keyChain;
		this.queue = queue;
		this.certificateName = certificateName;
		this.messagesSentCountPerUser = messagesSentCountPerUser;
		this.numMessages = numMessages;
		this.sync = sync;
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
		ChronoChat.pumpFaceAwhile(face, awhile);
	}

	@Override
	public void run() {
		try {
			String testType = System.getProperty("runMock");
			if (testType == null || !testType.equals("true")) {
				log.log(Level.INFO, "RUNNING REAL CHAT TEST.");
				this.chat = new ChronoChatTester(screenName, chatRoom,
					new Name(hubPrefix), face, keyChain, certificateName, sync);
			}
			else {
				log.log(Level.INFO, "RUNNING MOCK CHAT TEST.");
				this.chat = new MockChronoChatTester(screenName, chatRoom,
					new Name(hubPrefix), face, keyChain, certificateName, sync);
			}

			chat.setTestContext(this, numMessages, participantNo,
				participants, baseScreenName);
			chat.pumpFaceAwhile(5000);

			//create thread pool to
			//1. create chats and send series of predefined messages n times then send leave command.
			//2. Verify that each message was received n times from each thread.
			//3. each thread print metric: for each chat participant number received over total.
			int totalMessagesSent = 0;
			ArrayList<String> messages = getMessages(numMessages);

			for (String m : messages) {
				long chatDelayTime = chat.getChatDelayTime();
				long startTime = System.currentTimeMillis();
				long timeNow = System.currentTimeMillis();

				boolean sentMessage = false;
				while (!sentMessage) {
					if  ((timeNow - startTime) >= chatDelayTime) {
						chat.sendMessage(m);
						++totalMessagesSent;
						//messagesSentCountPerUser is shared in
						// every thread but each thread only writes to
						// one cell.
						messagesSentCountPerUser[participantNo] =  totalMessagesSent;
						sentMessage = true;
					}
					chat.pumpFaceAwhile(10);
					timeNow = System.currentTimeMillis();
				}
			}

			int numMessagesEachUserMustSend = numMessages;
			while(allUsersHaveNotSentAllMessages(numMessagesEachUserMustSend)) {
				chat.pumpFaceAwhile(3000);
			}
			chat.pumpFaceAwhile(15000); // for 3 sync lifetimes just to be sure.
			leave(chat);
			chat.submitStats(queue, numMessages);

			if (allUsersHaveNotSentAllMessages(numMessagesEachUserMustSend)) {
				log.log(Level.SEVERE, " Failed to conduct valid experiment. " +
							"Not all messages were sent in chat room, " +
							"results invalid.");
				System.exit(1);
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, null, e);
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
