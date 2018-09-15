package net.named_data.jndn.tests;

import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnRegisterFailed;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.sync.ChronoSync2013;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ChronoSyncClassic implements SyncAdapter {
	private static final String TAG = ChronoSyncClassic.class.getName();
	private static final Logger log = LoggerFactory.getLogger(ChronoSyncClassic.class);

	ChronoSync2013 sync_;
	public ChronoSyncClassic(ChronoSync2013.OnReceivedSyncState onReceivedSyncState,
													 ChronoSync2013.OnInitialized onInitialized,
													 Name chatPrefix_,
													 Name broadcastPrefix,
													 int session,
													 Face face,
													 KeyChain keyChain,
													 Name certificateName,
													 double syncLifetime_,
													 OnRegisterFailed onRegisterFailed_) {
		try {
			sync_ = new ChronoSync2013(
				onReceivedSyncState,
				(ChronoSync2013.OnInitialized) onInitialized,
				chatPrefix_,
				broadcastPrefix,
				session,
				face,
				keyChain,
				certificateName,
				syncLifetime_,
				onRegisterFailed_);
		} catch (IOException e) {
			log.error( "Failed to initialize sync.", e);
		} catch (SecurityException e) {
			log.error( "Failed to initialize sync.", e);
		}
	}

	@Override
	public void publishNextMessage(Data data) {
	}

	@Override
	public long getProducerSequenceNo(String prefix_, long sessionNo_) {
		return sync_.getProducerSequenceNo(prefix_, sessionNo_);
	}

	@Override
	public void publishNextSequenceNo() {
		try {
			sync_.publishNextSequenceNo();
		} catch (IOException e) {
			log.error( "Failed to publishNextSequenceNo.", e);
		} catch (SecurityException e) {
			log.error( "Failed to publishNextSequenceNo.", e);
		}

	}

	@Override
	public long getSequenceNo() {
		return sync_.getSequenceNo();
	}
}
