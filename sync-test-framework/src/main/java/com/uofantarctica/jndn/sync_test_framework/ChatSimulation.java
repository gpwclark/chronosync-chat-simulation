package com.uofantarctica.jndn.sync_test_framework;

import com.uofantarctica.jndn.helpers.FaceSecurity;
import com.uofantarctica.jndn.sync_test_framework.SyncQueue;
import com.uofantarctica.jndn.helpers.TransportConfiguration;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.uofantarctica.jndn.helpers.FaceSecurity.initFaceAndGetSecurityData;

public class ChatSimulation {
	private static final Logger log = LoggerFactory.getLogger(ChatSimulation.class);
	final int participants;
	final int numMessages;
	final String broadcastBaseName;
	final String screenName;
	final String hubPrefix;
	final String chatRoom;
	final int[] messagesSentCountPerUser;
	final SyncQueue<ArrayList<UserChatSummary>> resultQueue = new SyncQueue<>(5);
	private List<Interest> interestExpressed;

	public ChatSimulation(int participants, int numMessages, String broadcastBaseName, String screenName, String
			hubPrefix, String chatRoom) {
		this.participants = participants;
		this.numMessages = numMessages;
		this.broadcastBaseName = broadcastBaseName;
		this.screenName = screenName;
		this.hubPrefix = hubPrefix;
		this.chatRoom = chatRoom;
		messagesSentCountPerUser = new int[participants];
		interestExpressed = new CopyOnWriteArrayList<>();
	}

	public List<Interest> getAllInterests() {
		return interestExpressed;
	}

	public UserChatSummary simulate() {
		UserChatSummary summary = null;
		ExecutorService executor = Executors.newFixedThreadPool(participants,
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable runnable) {
						Thread t = new Thread(runnable);
						t.setDaemon(true);
						t.setName("ChronoChatUsers");
						return t;
					}
				});

		for (int i = 0; i < participants; ++i) {
			Face face = TransportConfiguration.getFace();
			FaceSecurity.SecurityData securityData = initFaceAndGetSecurityData(face);
			ChronoChatUser.pumpFaceAwhile(face, 2000);

			ChronoChatUser chronoChatUser = new ChronoChatUser(i, participants,
					broadcastBaseName, screenName, chatRoom, hubPrefix, face, securityData, resultQueue,
					 messagesSentCountPerUser, numMessages, interestExpressed);

			executor.execute(chronoChatUser);
		}

		// gatherMetrics does not return until all chatter users have finished
		// sending all their chatter messages and published their results to the
		// resultQueue.
		try {
			UserChatSummary accumulator = gatherMetrics(participants, resultQueue);
			verifyValidExperiment(accumulator, participants);
			shutDownExperiment(executor);
			summary = accumulator;
		}
		catch (Exception e) {
			log.error("error finishing simulation.", e);
		}
		return summary;
	}

	public static UserChatSummary gatherMetrics(int participants, SyncQueue
			resultQueue) {
		UserChatSummary accumulator = null;
		ArrayList<UserChatSummary> newResults;
		int numOfUsersFinished = 0;
		int numUniqueChats = 0;
		while (numOfUsersFinished < participants) {
			try {
				newResults = (ArrayList<UserChatSummary>) resultQueue.deQ();
				if (newResults != null) {
					++numOfUsersFinished;
					if (newResults.size() > 0)
						log.debug("Another participant finished " + numOfUsersFinished);
					else
						log.debug("Another participant finished " + numOfUsersFinished +
								" results is of size 0.");
					for (UserChatSummary u : newResults) {
						++numUniqueChats;
						if (accumulator == null) {
							accumulator = u;
						} else {
							accumulator.plus(u);
						}
					}
				}
			}
			catch (Exception e) {
				log.error(" error in gather metrics.",e);
			}
		}
		accumulator.setAccumulationStats(numOfUsersFinished, numUniqueChats);
		return accumulator;
	}

	private static void verifyValidExperiment(UserChatSummary accumulator,
	                                          int numParticipants) {
		int expectedNumUniqueChats = numParticipants * (numParticipants - 1);
		int numUniqueChats = accumulator.getNumUniqueChats();

		if (numUniqueChats != expectedNumUniqueChats) {
			log.error("Invalid experiment the number of unique chats, " +
					numUniqueChats + ", that was recorded does not equal the " +
					"expected number of unique chats, " + expectedNumUniqueChats +
					". A 'unique chatter' is the view a given receiver has " +
					"of all sent messages from a different user'");
		}
	}

	public static void shutDownExperiment(ExecutorService executor) {
		//make sure we shut down executor
		executor.shutdown();

		try {
			if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
				log.error("Pool did not terminate");
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			log.error("error shutting down experiment", e);
		}
	}

	public boolean validate(UserChatSummary summary) {
		boolean valid = true;
		if (correctNumberOfMessageLogs(summary)) {
			valid = false;
		}
		if (correctNumberOfChats(summary)) {
			valid = false;
		}
		if (correctNumberOfMesages(summary)) {
			valid = false;
		}
		String results = summary.toString();
		log.debug(results);
		log.debug("FIN");
		return valid;
	}

	public boolean correctNumberOfMessageLogs(UserChatSummary summary) {
		boolean valid = true;
		if (summary.getAccumulationCount() != participants) {
			log.error("FAILED: to test chronochat, number of results " +
					"AND number of participants did not match:  count: " +
					summary.getAccumulationCount() + ". " +
					"participants " + + participants);
			valid = false;
		}
		return valid;
	}

	public boolean correctNumberOfChats(UserChatSummary summary) {
		boolean valid = true;
		if (summary.getNumUniqueChats() != UserChatSummary.getExpectedNumUniqueChats(participants)) {
			log.error("FAILED: number of unique chats (combinations of 2 in N number of users) not " +
					"equal" +
					"to expected number of unique chats.");
			valid = false;
		}
		return valid;
	}

	public boolean correctNumberOfMesages(UserChatSummary summary) {
		boolean valid = true;
		int expectedTotalCount = UserChatSummary.getExpectedTotalCount(participants, numMessages);
		if (expectedTotalCount != summary.getTotalCount()) {
			log.error("Expected Total Count: " + expectedTotalCount);
			valid = false;
		}
		return valid;
	}

}
