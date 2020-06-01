// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import client.*;
import common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 * @version July 2000
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String id, String host, int port) 
  {
    try 
    {
      client= new ChatClient(id, host, port, this);
    } 
    catch(IOException exception) 
    {
      display("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {
      BufferedReader fromConsole = 
        new BufferedReader(new InputStreamReader(System.in));
      String message;

      while (true) 
      {
        message = fromConsole.readLine();

        if (message.equals("#quit")) {
          client.quit();
        } else if (message.equals("#logoff")) {
          client.closeConnection();
        } else if (message.split(" ")[0].equals("#sethost")) {
          if (client.isConnected()) {
            display("Client logged in, logoff to set the host.");
          } else {
            String newHost = message.split(" ")[1];
            client.setHost(newHost);
            display("New host set to " + client.getHost());
          }
        } else if (message.split(" ")[0].equals("#setport")) {
          if (client.isConnected()) {
            display("Client logged in, logoff to set the port.");
          } else {
            int newPort = DEFAULT_PORT;
            try {
              newPort = Integer.parseInt(message.split(" ")[1]);
              client.setPort(newPort);
              display("New port set to " + client.getPort());
            } catch (NumberFormatException e) {
              display("Please enter port number in valid format.");
            }
          }
        } else if (message.equals("#login")) {
          if (client.isConnected()) {
            display("Already logged in.");
          } else {
            client.openConnection();
            client.login();
          }
        } else if (message.equals("#gethost")) {
          if (client.isConnected()) {
            display("current host name " + client.getHost());
          } else {
            display("client logged off, host N/A.");
          }
        } else if (message.equals("#getport")) {
          if (client.isConnected()) {
            display("current port number " + client.getPort());
          } else {
            display("client logged off, port N/A.");
          }
        } else if (!client.isConnected()) {
          display("logged off from the server, login again to send messages.");
        } else {

          client.handleMessageFromClientUI(message);
        }

        
      }
    } 
    catch (Exception ex) 
    {
      display
        ("Unexpected error while reading from client console!");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
    String loginid = "";
    String host = "localhost";
    int port = DEFAULT_PORT;  //The port number

    if (args.length == 1) {
      loginid = args[0];
    } else if (args.length == 3) {
      loginid = args[0];
      host = args[1];
      try {
        port = Integer.parseInt(args[2]);
      } catch (NumberFormatException e) {
        System.out.println("Invalid port number");
        return;
      }
    } else {
      System.out.println("invalid login");
      return;
    }

    // try {
    //   host = args[0];
    //   port = Integer.parseInt(args[1]); //Get port from command line
    // } catch(Throwable t) {
    //   host = "localhost";
    //   port = DEFAULT_PORT; //Set port to 5555
    // }

    ClientConsole chat = new ClientConsole(loginid, host, port);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class
