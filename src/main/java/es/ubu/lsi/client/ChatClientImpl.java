package es.ubu.lsi.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import es.ubu.lsi.common.ChatMessage;

/**
 * 
 * @author Miguel Arroyo
 *
 */
public class ChatClientImpl extends UnicastRemoteObject implements ChatClient {
	
	private static final long serialVersionUID = 1L;
	private int id;
	private String nickname;

	public ChatClientImpl(int id, String nickname) throws RemoteException {
		super();
		this.id = id;
		this.nickname = nickname;
	}

	@Override
	public int getId() throws RemoteException {
		return id;
	}

	@Override
	public void setId(int id) throws RemoteException {
		this.id = id;
	}

	@Override
	public void receive(ChatMessage msg) throws RemoteException {
		String message = null;
		
		if (msg.getNickname() != getNickName()) {
			message = msg.getMessage();
			
			System.out.println("> " + message);
		}
	}

	@Override
	public String getNickName() throws RemoteException {
		return nickname;
	}	
	
}
