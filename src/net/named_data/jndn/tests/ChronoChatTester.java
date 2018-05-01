package net.named_data.jndn.tests;

import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChronoChatTester extends ChronoChat implements ChronoChatTest {
	protected static Random rand = new Random();

	protected Map<String, Map<String, Integer>> userByMessageByMessageCount = new HashMap<>();
	protected Map<String, Integer> aChatLog = new HashMap<>();
	protected int particpantNo;
	protected int participants;

	public ChronoChatTester(String screenName, String chatRoom, Name hubPrefix, Face face, KeyChain keyChain, Name certificateName) {
		super(screenName, chatRoom, hubPrefix, face, keyChain, certificateName);
	}

	@Override
	public void recordMessageReceipt(String from, String msg) {
		incMessage(from, msg);
	}

	public long getChatDelayTime() {
		int range = 1000;
		int interval = 10;
		// 1 <= n <= range
		int n = rand.nextInt(range) + 1;

		//(1 * interval) ms <= chatDelayTime <= (range * interval) ms
		long chatDelayTime = (long) n * interval;
		return chatDelayTime;
	}


	private void incMessage(String name, String message){
		if (name.contains(userName_)) {
			Logger.getLogger(ChronoChat.class.getName()).log(Level.SEVERE, "Not " +
				"incrementing message to myself, it is me. I am: " +
				userName_ + "and I " + "tried to inc message from: " + name);
			return;
		}
		Map<String, Integer> aChatLog = getUserChatLogFromUserName(name);
		if (aChatLog == null) {
			Logger.getLogger(ChronoChat.class.getName()).log(Level.SEVERE, "Asked " +
				"for participant who was not in the map, illegal call: " +
				name);
			Logger.getLogger(ChronoChat.class.getName()).log(Level.SEVERE, "Valid " +
				"participants");

			System.exit(1);
		}
		incMessageOnLog(message, aChatLog);
	}

	public static void incMessageOnLog(String message, Map<String, Integer> aChatLog) {
		aChatLog.put(message, aChatLog.get(message) + 1);
	}

	private Map<String, Integer> getUserChatLogFromUserName(String name) {
		String theKey = getUsersKeyFromUserName(name);
		return userByMessageByMessageCount.get(theKey);
	}

	private String getUsersKeyFromUserName(String name) {
		String theKey = "";
		boolean found = false;
		for ( String key : userByMessageByMessageCount.keySet() ) {
			if (key.contains(name)) {
				theKey = key;
				found = true;
				break;
			}
		}
		if (!found) {
			Logger.getLogger(ChronoChat.class.getName()).log(Level.SEVERE,
				"Couldn't find user with name: " + name);
			System.exit(1);
		}
		return theKey;
	}

	@Override
	public void setTestContext(ChronoChatUser cu, int numMessages, int
		participantNo, int participants, String baseScreenName) {
		ArrayList<String> messages = cu.getMessages(numMessages);
		this.aChatLog = initChatLog(messages);
		this.particpantNo = participantNo;
		this.participants = participants;

		addUser(userName_);
	}

	public static Map<String, Integer> initChatLog(ArrayList<String> messages) {
		Map<String, Integer> aChatLog = new HashMap<>();
		for (String m : messages) {
			aChatLog.put(m, 0);
		}
		return aChatLog;
	}

	@Override
	public void updateUser(String oldName, String newName) {
		Logger.getLogger(ChronoChat.class.getName()).log(Level.INFO, "update user. " +
			"oldName" + oldName + ", new name: " + newName);
		Map<String, Integer> aChatLog = userByMessageByMessageCount.get(oldName);
		if (aChatLog == null) {
			Logger.getLogger(ChronoChat.class.getName()).log(Level.INFO, "need to " +
				"figure out what to do with updateUser, there was no userByMessageByMessageCount " +
				"for them");
			System.exit(1);
		}
		userByMessageByMessageCount.put(newName, copyAChatLog(aChatLog));
		userByMessageByMessageCount.remove(oldName);

	}

	public static Map<String, Integer> copyAChatLog(Map<String, Integer>
		                                                 oldChatLog) {
		Map<String, Integer> newChatLog = new HashMap<>();
		Iterator it = oldChatLog.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			newChatLog.put((String)pair.getKey(), (int)pair.getValue());
		}
		return newChatLog;
	}

	public Map<String, Integer> getBlankChatLog() {
		return copyAChatLog(aChatLog);
	}

	@Override
	public void addUser(String name) {
		if (name.length() > screenName_.length()) {
			if (userByMessageByMessageCount.get(name) == null) {
				Logger.getLogger(ChronoChat.class.getName()).log(Level.INFO,
					"adding user:" +
					" " + name + " within test context for " + screenName_);

				Map<String, Integer> newChatLog = copyAChatLog(aChatLog);
				userByMessageByMessageCount.put(name, newChatLog);
				Logger.getLogger(ChronoChat.class.getName()).log(Level.INFO,"participant(s) " + userByMessageByMessageCount.size());
			}
		}
		else {
			Logger.getLogger(ChronoChat.class.getName()).log(Level.SEVERE, "It " +
				"appears a userName without a session number was almost added" +
				" to the userByMessageByMessageCount. That username was: " + name);

		}
	}

	@Override
	public void submitStats(SyncQueue queue, int numMessages) {
		int messagesSize = numMessages;
		Logger.getLogger(ChronoChat.class.getName()).log(Level.INFO,"Expected " + messagesSize + " messages");

		ArrayList<UserChatSummary> values = new ArrayList<>();

		int duplicates = 0;
		int numLost = 0;

		Iterator it = userByMessageByMessageCount.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry anIndividualsChatData = (Map.Entry) it.next();
			Map<String, Integer> userChatLog =
				(Map<String, Integer>) anIndividualsChatData.getValue();

			Iterator ite = userChatLog.entrySet().iterator();

			String userName = (String) anIndividualsChatData.getKey();
			if (userName.contains(screenName_))
				continue;

			Logger.getLogger(ChronoChat.class.getName()).log(Level.INFO,"submitStats from within " + screenName_ + " " +
				"for " + userName);
			Logger.getLogger(ChronoChat.class.getName()).log(Level.INFO,"reported number of unique messages: " + userChatLog
				.size());
			//TODO there is a case where one unique message got recorded 0 times.
			int currDupes = 0;
			int currNumLost = 0;
			int currCount = 0;
			StringBuffer individualResults = new StringBuffer();

			individualResults.append(" [ ");
			while (ite.hasNext()) {
				Map.Entry pair = (Map.Entry) ite.next();
				int count = (int) pair.getValue();
				if (count > 1) {
					int newDupes = count - 1;
					duplicates += newDupes;
					currDupes += newDupes;
					individualResults.append(", +" + Integer.toString(newDupes));
				} else if (count < 1) {
					int newNumLost = 1 - count;
					currNumLost -= newNumLost;
					numLost -= newNumLost;
					individualResults.append(", -" + Integer.toString(newNumLost));
				} else {
					individualResults.append(", 0");
				}
				currCount += count;
			}
			individualResults.append(" ] ");
			Logger.getLogger(ChronoChat.class.getName()).log(Level.INFO, individualResults
				.toString());
			Logger.getLogger(ChronoChat.class.getName()).log(Level.INFO,"");
			Logger.getLogger(ChronoChat.class.getName()).log(Level.INFO,"count: " + currCount);
			Logger.getLogger(ChronoChat.class.getName()).log(Level.INFO,"duplicates: " + currDupes);
			Logger.getLogger(ChronoChat.class.getName()).log(Level.INFO,"numLost: " + currNumLost);

			values.add(new UserChatSummary(userName, currCount, currDupes, currNumLost));
		}

		queue.enQ(values);
	}
}
