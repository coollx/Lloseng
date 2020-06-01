// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import ocsf.server.*;
import common.*;
/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  ChatIF serverUI; 
  boolean closed;
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
    this.closed = false;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
    String m = (String)msg;
    String dspId = client.toString();
    if (m.length() != 0  && 
        m.charAt(0) == '#' && 
        m.split(" ").length == 1) { //when the message is an id 
      if (client.getInfo("id") == null) {
        client.setInfo("id", m);
      } else {
        try {
          client.sendToClient("message start with `#` is not allowed");
        } catch (Exception e) {}
      }
    } else { //normal case
      if (client.getInfo("id") == null) {
        try {
          client.sendToClient("login information missing");
          client.close();
        } catch (Exception e) {}
      } else {
        String userId = (String)client.getInfo("id");
        dspId = (new StringBuffer(userId)).deleteCharAt(0).toString();
        this.sendToAllClients(dspId + ": " + m);
      }
    }

    serverUI.display("Message received: " + msg + " from " + dspId);
  }

  public void handleMessageFromServer(Object msg) {
    String prefix = "SERVER MSG>";
    serverUI.display(prefix + msg);
    this.sendToAllClients(prefix + msg);
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    this.closed = false;
    serverUI.display("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    serverUI.display("Server has stopped listening for connections.");
    this.sendToAllClients("WARNING - The server has stopped listening for connections");
  }

  /**
   * Hook method called when the server is clased.
   * The default implementation does nothing. This method may be
   * overriden by subclasses. When the server is closed while still
   * listening, serverStopped() will also be called.
   */
  protected void serverClosed() {
    this.closed = true;
  }

  public boolean isClosed() {
    return this.closed;
  }
  
  //Class methods ***************************************************
  
  // /**
  //  * This method is responsible for the creation of 
  //  * the server instance (there is no UI in this phase).
  //  *
  //  * @param args[0] The port number to listen on.  Defaults to 5555 
  //  *          if no argument is entered.
  //  */
  // public static void main(String[] args) 
  // {
  //   int port = 0; //Port to listen on

  //   try
  //   {
  //     port = Integer.parseInt(args[0]); //Get port from command line
  //   }
  //   catch(Throwable t)
  //   {
  //     port = DEFAULT_PORT; //Set port to 5555
  //   }
	
  //   EchoServer sv = new EchoServer(port);
    
  //   try 
  //   {
  //     sv.listen(); //Start listening for connections
  //   } 
  //   catch (Exception ex) 
  //   {
  //     System.out.println("ERROR - Could not listen for clients!");
  //   }
  // }

  protected void clientConnected(ConnectionToClient client) {
    serverUI.display(client + " is connected!");
  }

  synchronized protected void clientDisconnected(ConnectionToClient client) {
    serverUI.display(client.getInfo("id") + " disconnected!");
  }

  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
    clientDisconnected(client);
  }
}
//End of EchoServer class
