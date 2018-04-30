package net.named_data.jndn.tests;

import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChronoChatUser implements Runnable {

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

	public ChronoChatUser(int participantNo, int participants,
	                      String baseScreenName, String chatRoom, String
		                      hubPrefix, Face face, KeyChain keyChain,
	                      SyncQueue queue, Name
		                      certificateName, int[]
		                      messagesSentCountPerUser,
	                      int numMessages) {
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

	public static void leave(Chat chat, Face face) {
		// The user entered the command to leave.
		try {
			chat.leave();
			// Wait a little bit to allow other applications to fetch the leave message.
			double startTime = ChronoChat.getNowMilliseconds();
			while (true) {
				if (ChronoChat.getNowMilliseconds() - startTime >= 1000.0)
					break;

				face.processEvents();
				Thread.sleep(10);
			}
		} catch (Exception e) {
			Logger.getLogger(ChronoChat.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public static void pumpFaceAwhile(Face face, long awhile) {
		ChronoChat.pumpFaceAwhile(face, awhile);
	}

	@Override
	public void run() {
		try {
			//this.chat = new ChronoChatTester(screenName, chatRoom,
			// new Name(hubPrefix), face, keyChain, certificateName);
			this.chat = new MockChronoChatTester(screenName, chatRoom,
				new Name(hubPrefix), face, keyChain, certificateName);

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
					/*
					face.processEvents();

					Thread.sleep(10);
					*/
					timeNow = System.currentTimeMillis();
				}
			}

			int numMessagesEachUserMustSend = numMessages;
			while(allUsersHaveNotSentAllMessages(numMessagesEachUserMustSend)) {
				chat.pumpFaceAwhile(3000);
			}
			chat.pumpFaceAwhile(10000);
			leave(chat, face);
			chat.submitStats(queue, numMessages);

			if (allUsersHaveNotSentAllMessages(numMessagesEachUserMustSend)) {
				Logger.getLogger(ChronoChatUser.class.getName()).log
					(Level.SEVERE, " Failed to conduct valid experiment. " +
							"Not all messages were sent in chat room, " +
							"results invalid.");
				System.exit(1);
			}

		} catch (Exception e) {
			Logger.getLogger(ChronoChatUser.class.getName()).log(Level.SEVERE, null, e);
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
		return i + screenName + i;
	}
}
