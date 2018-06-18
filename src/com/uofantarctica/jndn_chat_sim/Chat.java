package com.uofantarctica.jndn_chat_sim;

public interface Chat {
	void recordMessageReceipt(String from, String msg);
	void updateUser(String oldName, String newName);
	void addUser(String name);
	void sendMessage(String chatMessage);
	void leave();
	void pumpFaceAwhile(long awhile);
}
