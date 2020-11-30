package es.ubu.lsi.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.server.ChatServer;

/**
 * Recibe como argumentos el nickname, (clave de cifrado??), y el host remoto
 * (este último parámetro opcional).
 * 
 * Inicia el proceso de exportación del cliente remoto y de la resolución del
 * servidor de chat remoto, enlazando ambos objetos (método checkIn).
 * 
 * En un bucle, recibe las entradas de texto del usuario y en función del mismo,
 * invoca a los métodos remotos del servidor.
 * 
 * Cuando el usuario introduce el texto logout finaliza su ejecución.
 * 
 * @author Miguel Arroyo
 *
 */
public class ChatClientStarter {

	/**
	 * Id del usuario
	 */
	private static int id;
	
	/**
	 * Si el cliente sigue activo para enviar mensajes
	 */
	private static boolean carryOn = true;


	public ChatClientStarter(String[] args) {
		// direccecion servidor 
		String serverHost = null; // POr defecto
		// nombre de usuario
		String username = null;

		if (args.length == 2) {
			username = args[0]; 
			serverHost = args[1];
		} else {
			username = args[0];
			serverHost ="localhost";
		}
		start(username, serverHost);
	}

	
	public boolean start(String username, String serverHost){
		
		// Se crea un cliente pasando un id y el nombre de usuario
		ChatClientImpl cliente = null;
		ChatServer servidor = null;

		try {
			cliente = new ChatClientImpl(id, username);
			//System.out.println("Cliente creado");
			// creamos el servidor "rmi://localhost:8080/Servidor"
			servidor = (ChatServer) Naming.lookup("rmi://"+serverHost+"/ChatServerImpl");
			//System.out.println("Servidor creado");
			// invocamos metodos del servidor, registramos un nuevo cliente
			//resolución del servidor de chat remoto, enlazando ambos objetos (método checkIn
			id = servidor.checkIn(cliente);
			cliente.setId(id);
			System.out.println("Bienvenido, " + cliente.getNickName());
			
		} catch (RemoteException e) {
			System.err.println("Excepción remota en ChatClientStarter: " +e.toString());
		} catch (MalformedURLException e) {
			System.err.println("Ecepción MalformedURLException, la url no existe: " + e.toString());
		} catch (NotBoundException e) {
			System.err.println("Excepcion NotBoundException, no existe ChatServer: " + e.toString());
		}
		
		sendMessages(cliente, servidor);
		return true;
	}
	
	private void sendMessages(ChatClientImpl cliente, ChatServer servidor) {
		/**
		 * Lee la entrada estandar por teclado del usuaio
		 */
		Scanner sc = new Scanner(System.in);
			
    	// En un bucle, recibe las entradas de texto del usuario y en función del mismo, invoca a los métodos remotos del servidor
    	while (carryOn) {
    		String userInput = sc.nextLine();
			try {
				if (userInput.equals("logout")) {
					//Se desconecta invocando metodo del cliente
					servidor.logout(cliente); // puede lanzar RemoteException
					disconnect();
					carryOn = false;
				}else if (userInput.equals("users")) {
					ChatMessage msg = new ChatMessage(id, cliente.getNickName(), userInput);
					// Mira los usuarios que hay actuamente conectados
					servidor.seeConectedUsers(msg);
				} else {
					ChatMessage msg = new ChatMessage(id, cliente.getNickName(), userInput);
					servidor.publish(msg);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	sc.close();
	}
		

	private static void disconnect() {
		carryOn = false;
		System.out.println("Me desconecto");
		System.exit(0); // Se sale correctamante
	}
}
