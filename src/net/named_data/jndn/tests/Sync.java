package net.named_data.jndn.tests;

import com.uofantarctica.dsync.DSync;
import net.named_data.jndn.Face;
import net.named_data.jndn.OnData;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.sync.ChronoSync2013;

import java.util.logging.Logger;

public class Sync implements SyncAdapter {
	private static final String TAG = Sync.class.getName();
	private static final Logger log = Logger.getLogger(TAG);

	private final DSync dsync;

	public Sync(OnData onData, ChronoSync2013.OnInitialized onInitialized, String dataPrefix, String broadcastPrefix, int
		session, Face face, KeyChain keyChain, String chatRoom, String screenName) {
		dsync = new DSync(onData,
			onInitialized,
			dataPrefix,
			broadcastPrefix,
			session,
			face,
			keyChain,
			chatRoom,
			screenName);
	}

	@Override
	public void publishNextMessage(long seqNo, ChatbufProto.ChatMessage.ChatMessageType messageType, String message, double time) {
		dsync.publishNextMessage(seqNo, messageType, message, time);
	}

	@Override
	public long getProducerSequenceNo(String prefix_, long sessionNo_) {
		return 0;
	}

	@Override
	public void publishNextSequenceNo() {

	}

	@Override
	public long getSequenceNo() {
		return 0;
	}
}
