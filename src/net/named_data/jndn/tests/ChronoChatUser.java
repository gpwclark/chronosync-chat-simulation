package net.named_data.jndn.tests;

import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChronoChatUser implements Runnable {

	static ArrayList<String> generatedMessages = null;
	String screenName;
	String chatRoom;
	String hubPrefix;
	Face face;
	KeyChain keyChain;
	int participants;
	int participantNo;
	Name certificateName;
	SyncQueue queue;
	ChatTester chat;
	int[] messagesSentCountPerUser;
	int numMessages;
	static Random rand = new Random();

	public ChronoChatUser(int participantNo, int participants,
	                      String screenName, String chatRoom, String
		                      hubPrefix, Face face, KeyChain keyChain,
	                      SyncQueue queue, Name
		                      certificateName, int[]
		                      messagesSentCountPerUser,
	                      int numMessages) {
		this.participantNo = participantNo;
		this.participants = participants;
		this.screenName = screenName;
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

	public static long getChatDelayTime() {
		int range = 1000;
		int interval = 10;
		// 1 <= n <= range
		int n = rand.nextInt(range) + 1;

		//(1 * interval) ms <= chatDelayTime <= (range * interval) ms
		long chatDelayTime = (long) n * interval;
		return chatDelayTime;
	}

	public static void pumpFaceAwhile(Face face, long awhile) {
		long startTime0 = System.currentTimeMillis();
		long timeNow0 = System.currentTimeMillis();
		while((timeNow0 - startTime0) <= awhile) {
			timeNow0 = System.currentTimeMillis();
			try {
				face.processEvents();
				Thread.sleep(10);
			}
			catch (Exception e) {
				Logger.getLogger(Chat.class.getName()).log(Level.SEVERE,
					"failed in pumpFaceAwhile", e);
			}
		}
	}

	public static void leave(Chat chat, Face face) {
		// The user entered the command to leave.
		try {
			chat.leave();
			// Wait a little bit to allow other applications to fetch the leave message.
			double startTime = Chat.getNowMilliseconds();
			while (true) {
				if (Chat.getNowMilliseconds() - startTime >= 1000.0)
					break;

				face.processEvents();
				Thread.sleep(10);
			}
		} catch (Exception e) {
			Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@Override
	public void run() {
		try {
			this.chat = new ChatTester(screenName, chatRoom, new Name
				(hubPrefix),	face, keyChain,
				certificateName);
			chat.setTestContext(this, numMessages, participantNo,
				participants);
			pumpFaceAwhile(face, 5000);

			//create thread pool to
			//1. create chats and send series of predefined messages n times then send leave command.
			//2. Verify that each message was received n times from each thread.
			//3. each thread print metric: for each chat participant number received over total.
			int totalMessagesSent = 0;
			ArrayList<String> messages = getMessages(numMessages);

			for (String m : messages) {
				long chatDelayTime = getChatDelayTime();
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
					face.processEvents();

					Thread.sleep(10);
					timeNow = System.currentTimeMillis();
				}
			}

			int numMessagesEachUserMustSend = numMessages;
			while(allUsersHaveNotSentAllMessages(numMessagesEachUserMustSend)) {
				pumpFaceAwhile(face, 3000);
			}
			pumpFaceAwhile(face, 10000);
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
}
