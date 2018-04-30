package net.named_data.jndn.tests;

import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MockChronoChatTester extends ChronoChatTester implements ChronoChatTest {

	public MockChronoChatTester(String screenName, String chatRoom, Name hubPrefix, Face face, KeyChain keyChain, Name certificateName) {
		super(screenName, chatRoom, hubPrefix, face, keyChain, certificateName);
	}

	@Override
	public void setTestContext(ChronoChatUser cu, int numMessages, int
		participantNo, int participants, String baseScreenName) {

		Logger.getLogger(MockChronoChatTester.class.getName()).log(Level
			.INFO, "Mocking test data");

		Map<String, Integer> testCounts = getPerfectTestCounts(cu, numMessages);
		//super.setTestContext(cu, numMessages, participantNo, participants, baseScreenName);
		for (int i = 0; i < participants; ++i) {
			String screenName = ChronoChatUser.generateScreenName (baseScreenName, i);
			if (i != participantNo) {
				userByMessageByMessageCount.put(screenName, testCounts);
			}
		}
	}

	public Map<String, Integer> getPerfectTestCounts(ChronoChatUser cu,
	                                                 int numMessages) {
		Map<String, Integer> perfectTestCounts = new HashMap<>();
		for (String m: cu.getMessages(numMessages)) {
			perfectTestCounts.put(m, 1);
		}
		return perfectTestCounts;
	}

	//TODO between setTestContext and submitStats need a fully and correctly
	// populated userMessageByMessageCount... only issue is getting the lists
	// of userNames? any way we can get around that?

	@Override
	public void submitStats(SyncQueue queue, int numMessages) {
		super.submitStats(queue, numMessages);
	}

	@Override
	public void recordMessageReceipt(String from, String msg) {
		Logger.getLogger(MockChronoChatTester.class.getName()).log(Level
			.INFO, "STUB!");
	}

	@Override
	public void updateUser(String oldName, String newName) {
		Logger.getLogger(MockChronoChatTester.class.getName()).log(Level
			.INFO, "STUB!");
	}

	@Override
	public void addUser(String name) {
		Logger.getLogger(MockChronoChatTester.class.getName()).log(Level
			.INFO, "STUB!");
	}

	@Override
	public void sendMessage(String chatMessage) {
		Logger.getLogger(MockChronoChatTester.class.getName()).log(Level
			.INFO, "STUB!");
	}

	@Override
	public void leave() {
		Logger.getLogger(MockChronoChatTester.class.getName()).log(Level
			.INFO, "STUB!");
	}

	@Override
	public void pumpFaceAwhile(long awhile) {
		Logger.getLogger(MockChronoChatTester.class.getName()).log(Level
			.INFO, "STUB!");
	}

	@Override
	public long getChatDelayTime() {
		return 0;
	}
}
