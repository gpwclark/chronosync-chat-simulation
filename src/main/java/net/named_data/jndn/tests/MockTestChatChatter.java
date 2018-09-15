package net.named_data.jndn.tests;

import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MockTestChatChatter extends TestChatChatter implements TestChat {
	private static final Logger log = LoggerFactory.getLogger(MockTestChatChatter.class);

	private String baseScreenName;
	private ArrayList<String> messages;
	private Map<String, Integer> sentMessageChatLog;

	public MockTestChatChatter(String screenName, String chatRoom, Name hubPrefix, Face face, KeyChain keyChain, Name certificateName) {
		super(screenName, chatRoom, hubPrefix, face, keyChain, certificateName);
	}

	@Override
	public void setTestContext(ChronoChatUser cu, int numMessages, int
		participantNo, int participants, String baseScreenName) {

		super.setTestContext(cu, numMessages, participantNo, participants, baseScreenName);

		if (participantNo >= participants) {
			log.error( "Invlaid participantNo: " + participantNo + ". The " +
					"particpnatNo can't be greater than or equal to: " +
				participants);
				System.exit(1);
		}

		log.debug( "Mocking test data");
		this.baseScreenName = baseScreenName;
		this.messages = cu.getMessages(numMessages);
		this.sentMessageChatLog = TestChatChatter.initChatLog(messages);

		Map<String, Integer> aChatLog = getPerfectTestCounts();
		for (int i = 0; i < participants; ++i) {
			String screenName = ChronoChatUser.generateScreenName(baseScreenName, i);
			if (i != participantNo) {
				userByMessageByMessageCount.put(screenName, aChatLog);
			}
		}

		if (userByMessageByMessageCount.size() != (participants - 1)) {
			log.error( "After mocking, the userByMessageByMessage count " +
				"map did not have correct number of elements, needed: " +
				numMessages + ", but had: " + userByMessageByMessageCount.size());
			System.exit(1);
		}
	}


	public Map<String, Integer> getPerfectTestCounts() {
		Map<String, Integer> perfectTestCounts = new HashMap<>();
		for (String m: messages) {
			perfectTestCounts.put(m, 1);
		}
		return perfectTestCounts;
	}

	//TODO between setTestContext and submitStats need a fully and correctly
	// populated userMessageByMessageCount... only issue is getting the lists
	// of userNames? any way we can get around that?

	@Override
	public void submitStats(SyncQueue queue, int numMessages) {
		for (String key : sentMessageChatLog.keySet()) {
			if (sentMessageChatLog.get(key) != 1) {
				log.error( "Failed to send all messages, make sure each " +
					"chronochat user fires off each message once.");
				System.exit(1);
			}

		}

		if (userByMessageByMessageCount.size() != (participants - 1)) {
			log.error( "Do not have all users chatter data.");
			System.exit(1);

		}
		super.submitStats(queue, numMessages);
	}

	@Override
	public void recordMessageReceipt(String from, String msg) {
		log.debug( "STUB!");
	}

	@Override
	public void updateUser(String oldName, String newName) {
		log.debug( "STUB!");
	}

	@Override
	public void addUser(String name) {
		log.debug( "STUB!");
	}

	@Override
	public void sendMessage(String chatMessage) {
		TestChatChatter.incMessageOnLog(chatMessage, this.sentMessageChatLog);

	}

	@Override
	public void leave() {
		log.debug( "STUB!");
	}

	@Override
	public void pumpFaceAwhile(long awhile) {
		log.debug( "STUB!");
	}

	@Override
	public long getChatDelayTime() {
		return 0;
	}
}
