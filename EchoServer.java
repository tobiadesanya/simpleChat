// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import common.ChatIF;
import ocsf.server.*;

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
public class EchoServer extends AbstractServer {
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
//Instance variables *************************************************
  
  /**
   *The interface type variable.  It allows the implementation of 
   * the display method on the server side.
   */
  ChatIF serverUI;
  
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
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client) {

	  String message = msg.toString();
	  
	  if ( message.startsWith("#") ) {
		  String[] args = message.split(" ");
		  if (args.length > 2) {
			  try {
				client.sendToClient("Error: Invalid command, please try again.");
			} catch (IOException e) {
				serverUI.display("Unable to send message to client");
			}
		  } else {
			  String command = args[0];
			  String arg = args[1];
			  
			  if (command.equals("#login")) {
				
				 if (client.getInfo("loginID") == null) {
					client.setInfo("loginID", arg);
					
				} else {
					try {
						client.sendToClient("Error: Invalid command. Terminating program."); //FIX
						close();
						System.exit(0);
					} catch (IOException e) {
						System.exit(0);
					}
				}
			  }
		  }
		  
	  } else {
		  
		  if (client.getInfo("loginID") == null) {
			  try {
				client.sendToClient("Error: You need to set a loginID.");
			} catch (IOException e) {
				serverUI.display("Unable to send message to client");
			}
		  } else {
			  serverUI.display("Message received: " + msg + " from " + client.getInfo("loginID").toString());
			  this.sendToAllClients( client.getInfo("loginID").toString() + ": " + msg);
		  }
	  }
  }
  
  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromServerUI(String message) {
	  if ( message.startsWith("#") ) {
		  String[] args = message.split(" ");
		  if (args.length > 2) {
			  serverUI.display("Error: Invalid command, please try again.");
		  } else {
		  String command = args[0];
		  switch (command) {
			case "#quit": 
				stopListening();
				try {
					this.close();
				} catch (IOException e1) {
					System.exit(0);
				}
				System.exit(0);
				break;
			case "#stop":
				try {
					stopListening();
				} catch (Exception e) {
					serverUI.display("Error: unable to stop listening for new clients.");
				}
				break;
			case "#close":
				try {
					stopListening();
					close();
				} catch (Exception e) {
					serverUI.display("Error: unable to close server.");
				}
				break;
			case "#setport":
				if (this.isListening() || this.getNumberOfClients() > 0) {
					serverUI.display("Error: you cannot set the port unless the server is closed.");
				} else {
					if (args.length == 1) {
						serverUI.display("Error: Invalid command.");
					} else {
						this.setPort( Integer.parseInt(args[1]) );
					}
				}
				break;
			case "#start":
				if (this.isListening()) {
					serverUI.display("Error: you are already listening for new clients.");
				} else {
					try {
						this.listen();
					} catch (Exception e) {
						serverUI.display("Error: unable to start listening for new clients.");
					}
				}
				break;
			case "#getport":
				System.out.println( this.getPort() );
				break;
			default:
				serverUI.display("Error: Invalid command, please try again.");
				break;
			}
		  }
		  
	  } else {
	  
    String completeMsg = "SERVER MSG> " + message;
    serverUI.display(completeMsg);
	sendToAllClients(completeMsg);
	
	  }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    serverUI.display
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    serverUI.display
      ("Server has stopped listening for connections.");
  }
  
  //Class methods ***************************************************
  
  /**
   * Implements the hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  serverUI.display("Welcome " + client.getInfo("loginID").toString() +  ". Thank you for connecting to the server.");
	  sendToAllClients("Welcome " + client.getInfo("loginID").toString() +  ". Thank you for connecting to the server.");
  }
  
  /**
   * Implements the hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  serverUI.display("We are sad to see you go. Thank you for connecting!");
	  sendToAllClients("We are sad to see you go. Thank you for connecting!");
  }
  
  /**
   * Implements the hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   * The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  @Override
  synchronized protected void clientException(
    ConnectionToClient client, Throwable exception) {
	  serverUI.display("We are sad to see you go. Thank you for connecting!");
	  sendToAllClients("We are sad to see you go. Thank you for connecting!");
  }
  
  
}
//End of EchoServer class
