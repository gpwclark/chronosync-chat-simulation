package com.uofantarctica.jndn_chat_sim;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UserChatSummary {
	private static final String TAG = UserChatSummary.class.getName();
	private static final Logger log = Logger.getLogger(TAG);

	private int totalCount;
	private int duplicates;
	private int numLost;
	private String name;

	private int numUsersFinished;
	private int numUniqueChats;

	public UserChatSummary(String name, int totalCount,
	                       int duplicates, int numLost) {
		this.name = name;
		this.totalCount = totalCount;
		this.duplicates = duplicates;
		this.numLost = numLost;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public int getDuplicates() {
		return duplicates;
	}

	public int getNumLost() {
		return numLost;
	}

	public void setAccumulationStats(int accumulationCount, int numUniqueChats) {
		this.numUsersFinished = accumulationCount;
		this.numUniqueChats = numUniqueChats;
	}

	public static int getExpectedTotalCount(int participants, int numMessages) {
		return (participants - 1) * numMessages * participants;
	}

	public void plus(UserChatSummary toAdd) {
		totalCount += toAdd.totalCount;
		duplicates += toAdd.duplicates;
		numLost += toAdd.numLost;
	}

	public int getNumUsersFinished() {
		return numUsersFinished;
	}

	public int getNumUniqueChats() {
		return numUniqueChats;
	}

	public void recordAccumulatedStats(StringBuffer sb) {
		append(sb, TAG, "Final Report");
		append(sb, TAG, "total number of messages received by all users in the chatroom: " + totalCount);
		append(sb, TAG, "total number of duplicate messages received by some user in chatroom: " + duplicates);
		append(sb, TAG, "total number of messages some users never received: " + numLost);
	}

	public void printFullReport(int participants, int numMessages) {
		StringBuffer sb = new StringBuffer();
		append(sb, TAG, "Expected total count: " + getExpectedTotalCount(participants, numMessages));
		recordAccumulatedStats(sb);
		log.log(Level.INFO, sb.toString());
	}

	public static void append(StringBuffer sb, String tag, String msg) {
		sb.append(tag + ": " + msg);
		sb.append("\n");
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		recordAccumulatedStats(sb);
		return sb.toString();
	}
}

