package es.ubu.lsi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.ubu.lsi.client.ChatClient;
import es.ubu.lsi.common.ChatMessage;


/**
 * Implementacion del objeto remoto
 * 
 * @author Miguel Arroyo
 *
 */
public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {
	

	private static final long serialVersionUID = 1L;
	/**
	 * Id de los usuario
	 */
	private static int clientId = 0;
	/**
	 * Formato de fecha para registra la hora de conexxion
	 */
	private static SimpleDateFormat sdf;
	/**
	 * Alamcenamoos el socket del cliente, su nombre de usuario y la hora en la que
	 * se conecta.
	 */
	private static ArrayList<Object> clientInfo;

	/**
	 * Registro de los clientes y su informacion
	 */
	private Map<Integer, ArrayList<Object>> clients = new HashMap<>();

	protected ChatServerImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int checkIn(ChatClient client) throws RemoteException {
		// Registramos un nuevo cliente
		Date date = new Date();
		sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println("Cliente conectado: " + client.getNickName() + " a las " + sdf.format(date));
		
		// Cada cliente que se registra tiene un id nuevo
		clientId++;
		
		// Array con nombre y fecha de conexion
		clientInfo = new ArrayList<Object>();
		clientInfo.add(0, client);
		clientInfo.add(1, client.getNickName());
		clientInfo.add(2, sdf.format(date));
		clients.put(clientId, clientInfo);
		
		
		return clientId;
	}

	@Override
	public void logout(ChatClient client) throws RemoteException {
		System.out.println("Removed the client " + client.getId()+ " : "+ client.getNickName());
		
		//Elimina del map al cliente pasado por parametro
		clients.remove(client.getId());
	}

	@Override
	public void publish(ChatMessage msg) throws RemoteException {
		String recieveMsg = msg.getMessage();
		Date date = new Date();
		sdf = new SimpleDateFormat("HH:mm:ss");
	
		for (Map.Entry<Integer, ArrayList<Object>> entry : clients.entrySet()) {
			String message = msg.getNickname() + "(" + sdf.format(date) +"): " + recieveMsg;
			// Se envia a todos menos a quien lo envia
			if (msg.getId() != entry.getKey()) {
				ChatClient cliente  = (ChatClient) entry.getValue().get(0);
				msg.setMessage(message);
				cliente.receive(msg);

			}
		}

	}
	
	@Override
	public void seeConectedUsers(ChatMessage msg) throws RemoteException {
		// System.out.println("Viendo los usuarios conectados: ");
		String message = "";
		
		for (Map.Entry<Integer, ArrayList<Object>> entry : clients.entrySet()) {
			if (msg.getId() != entry.getKey()) {
				message = message + entry.getValue().get(1) + " (" + entry.getValue().get(2) + "), ";
			}
		}
		msg.setMessage(message);
		for (Map.Entry<Integer, ArrayList<Object>> entry : clients.entrySet()) {
			// Se envia a todos menos a quien lo envia
			if (msg.getId() == entry.getKey()) {
				ChatClient cliente  = (ChatClient) entry.getValue().get(0);
				cliente.receive(msg);
			}
		}
	}
	
	/**
	 * Método no implementado
	 */
	@Override
	public void shutdown(ChatClient client) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
}
