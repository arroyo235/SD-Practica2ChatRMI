package es.ubu.lsi.server;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.server.RMIClassLoader;
import java.util.Properties;

/**
 * Inicia el proceso de exportación del servidor remoto y su registro en RMI
 * (rmiregistry).
 * 
 * @author Miguel Arroyo
 *
 */
public class ChatServerStarter {
	
	public ChatServerStarter() {
		// TODO Auto-generated constructor stub
		
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			Properties p = System.getProperties();
			// lee el codebase
			String url = p.getProperty("java.rmi.server.codebase");
			// Cargador de clases dinámico ...
			Class<?> serverClass = RMIClassLoader.loadClass(url,"es.ubu.lsi.server.ChatServerImpl");
			Naming.rebind("/ChatServerImpl", (Remote) serverClass.newInstance());
			//System.out.println("Servidor registrado...");
		} catch (Exception e) {
			System.err.println("Excepcion recogida: " + e.toString());
		}
		
	}

}
