package net.named_data.jndn.tests;

import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatTester extends Chat {

	Map<String, Map<String, Integer>> userByMessageByMessageCount = new HashMap<>();
	Map<String, Integer> testCounts = new HashMap<>();
	int particpantNo;
	int participants;

	public ChatTester(String screenName, String chatRoom, Name hubPrefix, Face face, KeyChain keyChain, Name certificateName) {
		super(screenName, chatRoom, hubPrefix, face, keyChain, certificateName);
	}

	public void recordMessageReceipt(String from, String msg) {
		incMessage(from, msg);
	}

	public void incMessage(String name, String message){
		if (name.contains(userName_)) {
			Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, "Not " +
				"incrementing message to myself, it is me. I am: " +
				userName_ + "and I " + "tried to inc message from: " + name);
			return;
		}
		Map<String, Integer> testCounts = getUsersTestCountsFromUserName(name);
		if (testCounts == null) {
			Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, "Asked " +
				"for participant who was not in the map, illegal call: " +
				name);
			Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, "Valid " +
				"participants");

			System.exit(1);
		}
		testCounts.put(message, testCounts.get(message) + 1);
	}

	public Map<String, Integer> getUsersTestCountsFromUserName(String name) {
		String theKey = getUsersKeyFromUserName(name);
		return userByMessageByMessageCount.get(theKey);
	}

	public String getUsersKeyFromUserName(String name) {
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
			Logger.getLogger(Chat.class.getName()).log(Level.SEVERE,
				"Couldn't find user with name: " + name);
			System.exit(1);
		}
		return theKey;
	}


	public void setTestContext(ChronoChatUser cu, int numMessages, int
		participantNo, int participants) {
		ArrayList<String> messages = cu.getMessages(numMessages);
		this.particpantNo = participantNo;
		this.participants = participants;
		for (String m : messages) {
			testCounts.put(m, 0);
		}
		addUser(userName_);
	}
	public void updateUser(String oldName, String newName) {
		Logger.getLogger(Chat.class.getName()).log(Level.INFO, "update user. " +
			"oldName" + oldName + ", new name: " + newName);
		Map<String, Integer> testCounts = userByMessageByMessageCount.get(oldName);
		if (testCounts == null) {
			Logger.getLogger(Chat.class.getName()).log(Level.INFO, "need to " +
				"figure out what to do with updateUser, there was no userByMessageByMessageCount " +
				"for them");
			System.exit(1);
		}
		userByMessageByMessageCount.put(newName, copyTestCounts(testCounts));
		userByMessageByMessageCount.remove(oldName);

	}

	public Map<String, Integer> copyTestCounts(Map<String, Integer> oldTestCounts) {
		Map<String, Integer> newTestCounts = new HashMap<>();
		Iterator it = oldTestCounts.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			newTestCounts.put((String)pair.getKey(), (int)pair.getValue());
		}
		return newTestCounts;
	}

	public void addUser(String name) {
		if (name.length() > screenName_.length()) {
			if (userByMessageByMessageCount.get(name) == null) {
				Logger.getLogger(Chat.class.getName()).log(Level.FINE,
					"adding user:" +
					" " + name + " within test context for " + screenName_);

				Map<String, Integer> newTestCounts = copyTestCounts(testCounts);
				userByMessageByMessageCount.put(name, newTestCounts);
				Logger.getLogger(Chat.class.getName()).log(Level.FINE,"participant(s) " + userByMessageByMessageCount.size());
			}
		}
		else {
			Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, "It " +
				"appears a userName without a session number was almost added" +
				" to the userByMessageByMessageCount. That username was: " + name);

		}
	}

	public void submitStats(SyncQueue queue, int numMessages) {
		int messagesSize = numMessages;
		Logger.getLogger(Chat.class.getName()).log(Level.FINE,"Expected " + messagesSize + " messages");

		ArrayList<UserChatSummary> values = new ArrayList<>();

		int duplicates = 0;
		int numLost = 0;

		Iterator it = userByMessageByMessageCount.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry anIndividualsChatData = (Map.Entry) it.next();
			Map<String, Integer> testCounts =
				(Map<String, Integer>) anIndividualsChatData.getValue();

			Iterator ite = testCounts.entrySet().iterator();

			String userName = (String) anIndividualsChatData.getKey();
			if (userName.contains(screenName_))
				continue;

			Logger.getLogger(Chat.class.getName()).log(Level.FINE,"submitStats from within " + screenName_ + " " +
				"for " + userName);
			Logger.getLogger(Chat.class.getName()).log(Level.FINE,"reported number of unique messages: " + testCounts
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
			Logger.getLogger(Chat.class.getName()).log(Level.FINE, individualResults
				.toString());
			Logger.getLogger(Chat.class.getName()).log(Level.FINE,"");
			Logger.getLogger(Chat.class.getName()).log(Level.FINE,"count: " + currCount);
			Logger.getLogger(Chat.class.getName()).log(Level.FINE,"duplicates: " + currDupes);
			Logger.getLogger(Chat.class.getName()).log(Level.FINE,"numLost: " + currNumLost);
			values.add(new UserChatSummary(userName,
				currCount, currDupes, currNumLost));
		}

		queue.enQ(values);
	}

}
