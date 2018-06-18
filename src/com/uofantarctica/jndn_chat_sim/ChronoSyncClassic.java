package com.uofantarctica.jndn_chat_sim;

import com.uofantarctica.dsync.model.SyncAdapter;
import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnRegisterFailed;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.sync.ChronoSync2013;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChronoSyncClassic implements SyncAdapter {
	private static final String TAG = ChronoSyncClassic.class.getName();
	private static final Logger log = Logger.getLogger(TAG);

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
			log.log(Level.SEVERE, "Failed to initialize sync.", e);
		} catch (SecurityException e) {
			log.log(Level.SEVERE, "Failed to initialize sync.", e);
		}
	}

	@Override
	public void publishNextMessage(long l, String s, String s1, double v) {

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
			log.log(Level.SEVERE, "Failed to publishNextSequenceNo.", e);
		} catch (SecurityException e) {
			log.log(Level.SEVERE, "Failed to publishNextSequenceNo.", e);
		}

	}

	@Override
	public long getSequenceNo() {
		return sync_.getSequenceNo();
	}

	@Override
	public long getSessionNo() {
		return 0;
	}

	@Override
	public void initSyncForDataSet(OnData onData, ChronoSync2013.OnInitialized onInitialized, String s, String s1) {

	}
}
