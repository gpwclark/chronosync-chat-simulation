package net.named_data.jndn.tests;

import com.uofantarctica.dsync.DSync;
import com.uofantarctica.dsync.model.ReturnStrategy;
import net.named_data.jndn.Data;
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
		ReturnStrategy strategy = ReturnStrategy.EXACT;
		dsync = new DSync(onData,
			onInitialized,
			dataPrefix,
			broadcastPrefix,
			session,
			face,
			keyChain,
			chatRoom,
			screenName,
			strategy);
	}

	@Override
	public void publishNextMessage(Data data) {
		dsync.publishNextMessage(data);
	}

	public String getStringVersionOfMessageType(ChatbufProto.ChatMessage.ChatMessageType type) {
		return type.toString();
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
