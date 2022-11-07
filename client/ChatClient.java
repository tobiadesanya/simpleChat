// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.loginID = loginID;
    this.clientUI = clientUI;
    openConnection();
    
    sendToServer("#login " + loginID);
    
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message) {
	  if ( message.startsWith("#") ) {
		  String[] args = message.split(" ");
		  if (args.length > 2) {
			  System.out.println("Error: Invalid command, please try again.");
		  } else {
		  String command = args[0];
		  switch (command) {
			case "#quit": 
				quit();
				System.exit(0);
				break;
			case "#logoff":
				try {
					closeConnection();
				} catch (Exception e) {
					System.out.println("Error: unable to disconnect from server");
				}
				break;
			case "#sethost":
				if (isConnected()) {
					System.out.println("Error: you cannot set the host if you are logged in.");
				} else {
					if (args.length == 1) {
						System.out.println("Error: Invalid command.");
					} else {
						this.setHost(args[1]);
					}
				}
				break;
			case "#setport":
				if (isConnected()) {
					System.out.println("Error: you cannot set the port if you are logged in.");
				} else {
					
					if (args.length == 1) {
						System.out.println("Error: Invalid command.");
					} else {
						this.setPort(Integer.parseInt(args[1]));
					}
				}
				break;
			case "#login":
				if (isConnected()) {
					System.out.println("Error: you are already logged in.");
				} else {
					try {
						this.openConnection();
						sendToServer("#login " + loginID);
					} catch (Exception e) {
						System.out.println("Error: unable to connect to server");
					}
				}
				break;
			case "#gethost":
				System.out.println( this.getHost() );
				break;
			case "#getport":
				System.out.println( this.getPort() );
				break;
			}
		  }
	  } else {
	  
		  try {
			  sendToServer(message);
		  } catch(IOException e) {
			  clientUI.display("Could not send message to server.  Terminating client.");
			  quit();
		  }
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }


/**
 * Implements the hook method called after the connection has been closed. The default
 * implementation does nothing. The method may be overridden by subclasses to
 * perform special processing such as cleaning up and terminating, or
 * attempting to reconnect.
 */
@Override
protected void connectionClosed() {
	clientUI.display("Connection closed.");
}

/**
 * Implements the hook method called each time an exception is thrown by the client's
 * thread that is waiting for messages from the server. The method may be
 * overridden by subclasses.
 * 
 * @param exception
 *            the exception raised.
 */
@Override
protected void connectionException(Exception exception) {
	clientUI.display("The server has shut down.");
	System.exit(0);
}


}



//End of ChatClient class
