
import java.io.*;
import common.*;

public class ServerConsole implements ChatIF {
	EchoServer server;

	public ServerConsole(int port) {
		try {
			server = new EchoServer(port, this);
			server.listen();
		} catch (IOException exception) {
			System.out.println("Error: Can't setup connection!" + " Terminating server.");
			System.exit(1);
		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}

	public void display(String message) {
    	System.out.println(">>> " + message);
  	}

  	public void accept() {
  		try {
  			BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
      		String message;

      		while (true) {
      			message = fromConsole.readLine();

      			if (message.equals("#quit")) {
      				//server.close();
      				System.exit(1);
      			} else if (message.equals("#stop")) {
      				server.stopListening();
      			} else if (message.equals("#close")) {
      				server.close();
      			} else if (message.split(" ")[0].equals("#setport")) {
      				if (!server.isClosed()) {
      					display("Need to close the server to set the port");
      				} else {
      					int newPort = EchoServer.DEFAULT_PORT;
      					try {
      						newPort = Integer.parseInt(message.split(" ")[1]);
      						server.setPort(newPort);
      						display("new port has been set to " + server.getPort());
      					} catch (Exception e) {
      						display("Port# invalid, enter another port#");
      					}
      				}
      			} else if (message.equals("#start")) {
      				if (!server.isListening()) {
      					server.listen();
      				} else {
      					display("server can start only if it has stopped");
      				}
      			} else if (message.equals("#getport")) {
      				if (server.isClosed()) {
      					display("server closed, port# N/A");
      				} else {
      					display("current port#: " + server.getPort());
      				}
      			} else {
      				server.handleMessageFromServer(message);
      			}
      		
      		}
  		} catch (Exception ex) {
  			display("Unexpected error while reading from server console!");
  		}
  	}

	public static void main(String[] args) {
    	int port = 0; //Port to listen on

    	try {
      		port = Integer.parseInt(args[0]); //Get port from command line
    	} catch (Throwable t) {
      		port = EchoServer.DEFAULT_PORT; //Set port to 5555
    	}
		
    	ServerConsole sc = new ServerConsole(port);
    	sc.accept();
    
  }

}