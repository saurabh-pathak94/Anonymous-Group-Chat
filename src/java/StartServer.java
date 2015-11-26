import java.rmi.*;
import java.rmi.server.*;
 
public class StartServer {
	public static void main(String[] args) {
		try {
			//System.setProperty("java.security.policy", "/server.policy");
				//System.setSecurityManager(new RMISecurityManager());
			 	java.rmi.registry.LocateRegistry.createRegistry(1099);
			 	
				ChatServerInt b=new ChatServer();	
				Naming.rebind("rmi://127.0.0.1/myabc", b);
				System.out.println("[System] Chat Server is ready.");
			}catch (Exception e) {
					System.out.println("Chat Server failed: " + e);
			}
	}
}