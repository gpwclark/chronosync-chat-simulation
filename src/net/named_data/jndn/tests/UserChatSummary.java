package net.named_data.jndn.tests;

class UserChatSummary {

	private int totalCount;
	private int duplicates;
	private int numLost;
	private String name;

	private int accumulationCount;
	private int numUniqueChats;

	public UserChatSummary(String name, int totalCount,
	                       int duplicates, int numLost) {
		this.name = name;
		this.totalCount = totalCount;
		this.duplicates = duplicates;
		this.numLost = numLost;
	}

	public void setAccumulationStats(int accumulationCount, int numUniqueChats) {
		this.accumulationCount = accumulationCount;
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

	public int getAccumulationCount() {
		return accumulationCount;
	}

	public int getNumUniqueChats() {
		return numUniqueChats;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		sb.append("Final Report");
		sb.append("\n");
		sb.append("total number of messages received by all users in the chatroom: " + totalCount);
		sb.append("\n");
		sb.append("total number of duplicate messages received by some user in chatroom: " + duplicates);
		sb.append("\n");
		sb.append("total number of messages some users never received: " + numLost);
		sb.append("\n");
		return sb.toString();
	}
}

