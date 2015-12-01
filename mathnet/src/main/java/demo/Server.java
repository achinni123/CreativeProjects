package demo;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

public class Server {
	
	public String rain(String args) {
		return args.toUpperCase();
	}

	public static void main(String[] args) {
		startServer(8082);
	}

	public static void startServer(int portNumber) {
		try {
			System.out.println("Attempting to start XML-RPC Server...");
			WebServer webServer = new WebServer(portNumber);
			XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
			PropertyHandlerMapping phm = new PropertyHandlerMapping();
			phm.addHandler("sample", Server.class); //new JavaServer().getClass());
			xmlRpcServer.setHandlerMapping(phm);
			XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
			serverConfig.setEnabledForExtensions(true);
			serverConfig.setContentLengthOptional(false);
//			serverConfig.setKeepAliveEnabled(true);
//			boolean res = serverConfig.isKeepAliveEnabled();
			webServer.start();
			System.out.println("Started successfully.");
			System.out.println("Accepting requests. (Halt program to stop.)");
		} catch (Exception exception) {
			System.err.println("JavaServer: " + exception);
		}
	}
}