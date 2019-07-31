/*  This file is JokeClientAdmin for the Joke Server project for Distributed System I

    1. Name / Date:  James Valles, April 21, 2019
    2. Java version used:  1.8
    3. Precise command-line compilation examples / instructions:

     Example:
     Compile first:
	    > javac JokeServer.java
      > javac JokeClient.java
	    > javac JokeClientAdmin.java

     Run JokeServer files:
     To launch only a primary server type: java JokeServer
     To launch a secondary server type: java JokeServer secondary
     To launch a JokeClient for only a primary server type: java JokeClient

     To launch a JokeClient for both a primary and secondary server type
     (for server name enter, 'localhost'): java JokeClient localhost localhost

   4. List of files needed for running the program.
      a.JokeServer.java
    	b. JokeClient.java
    	c. JokeClientAdmin.java
   	  d. JokeLog.txt
   	  e. checklist-joke.html.

   5. Notes:
	Thank you so much for grading this project. The version supports both secondary and primary servers for use with
	JokeClient and JokeClientAdmin. I tested using 140.192.1.9, but there seems to be a lag. So, for testing I recommend
	using localhost as servername for both primary and secondary servers.
  I added JOKE CYCLE COMPLETED & PROVERB CYCLE COMPLETED on both server/client outputs. When switching be servers,
  I have noticed, a few times ,where there may be a be a slight delay in retrieving jokes/proverbs. If you press
  return, again, this will flush and you will see all responses listed.
  I added additional spacing between jokes to make it easier to grade along with additional conventions such as "James Valles'
  Joke Server running" for clarity. Obviously, this is my first draft, will work on reformatting and organizing code in the future.
  Thank you for reviewing my work! :)
*/


import java.io.*; //used to retrieve the I/O libraries
import java.net.*; //used to retrieve the Java networking libraries

//This is the JokeClientAdmin Class
public class JokeClientAdmin {

  // Initializing static varibales to be used throughout all classes. I could do a better job handling this in the future, don't like using static.
  private static String mode;
  static boolean secondaryServer = false;
  static boolean updatingServerStatus = false;
  static boolean updatingMode = false;
  static boolean doThisOnce = true;
  static int port = 5050;
  static String serverName = "localhost";
  static String serverName2 = "localhost";

  public static void setMode(String mode) {
    JokeClientAdmin.mode = mode;
  }

  // This is the main method.
  public static void main(String[] args) {
    JokeClientAdmin admin = new JokeClientAdmin();
    //To ensure toggling starts with joke, then proverb mode
    String mode = "Proverb";
    //used to switch server ports
    Boolean switchserver = true;
    setMode(mode);


    // This prints to the screen at start of program
    System.out.println(("James Valles' JokeClientAdmin"));

    //If there are no args entered at command line (indicating primary server only, set port to 5050, and print this message)
    if (args.length < 1) {
      serverName = "localhost";
      // port = 5050;
      System.out.println("Server one: localhost, port 4545. JokeClientAdmin running at port 5050.");
    } else {

      //If 'secondary' entered at command line, (indicating secondary server, set ports, servername, and print messages)
      System.out.println("Server one: localhost, port 4545. JokeClientAdmin running at port 5050.");
      if (args.length == 2) {
        serverName2 = args[1];
        //port = 5051;
        System.out.println(
            "Server two: " + serverName2 + ", port 4546. JokeClientAdmin running at port 5051.");
      }
    }

    // Initializing bufferedreader so that we can read in data, we will also use input stream reader here,
    // InputStreamReader (reads bytes and decodes them into characters)
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    try {

      // Initialize new String variable adminInput which will capture what user enters at prompt, which is nothing.
      String adminInput = "";

//      if (doThisOnce) {
//        updatingServerStatus = true;
//        doThisOnce = false;
//        communicateWithServer(adminInput, serverName, port);
//      }

      // Instructions print to screen, 'quit' to exit, otherwise if s is entered toggle servers and update ports to 5050/5051.
      System.out.println(
          "\nPress return key to switch modes, or 's' to toggle servers, or type 'quit' to terminate program.");
      do {

        //write any remaining data stored in the out buffer
        System.out.flush();

        adminInput = in.readLine();
        //System.out.println("Variable up here" + JokeClientAdmin.secondaryServer);

        if (adminInput.indexOf("quit") < 0) {
          if (adminInput.equals("s")) {
            if (switchserver) {
              updatingMode = true;
              port = 5051;
              serverName = serverName2;
              System.out.println("Now communicating with localhost, port 4546.");
              switchserver = false;
              //   communicateWithServer(adminInput, serverName, port);

            } else {
              updatingMode = true;
              serverName = "localhost";
              switchserver = true;
              port = 5050;
              System.out.println("Now communicating with localhost, port 4545.");

              //   communicateWithServer(adminInput, serverName, port);
            }
            adminInput = "";
          } else {
            updatingMode = true;
            communicateWithServer(adminInput, serverName, port);
            adminInput = "";
          }
        }
//  Exit program if 'quit' entered
      } while (adminInput.indexOf("quit") < 0);
      //exit the program if 'quit' is entered
      System.out.println("User has terminated program.");
    } catch (IOException x) {
      //invoke function PrintStrackTrace on IOException object x to go up the stack and print where exception occurred. This will help developer understand where exception was raised
      x.printStackTrace();
    }
  }

     //This function handles all communication with server.
  static void communicateWithServer(String name, String serverName, int port) {
    Socket sock; //initialize a new socket . Socket is an endpoint.  (java object). Used for communication.
    BufferedReader fromServer; //initializing bufferedreader so that we can read in data from server
    PrintStream toServer; //initializing printstream so that we can write (send) data to server, flushes automatically
    String textFromServer; //initializing new String variable which will hold text from server

    try {
      // Create a new socket, using servername, and port 5050/5051, store in sock variable
      // Sock.getInputStream() retrieving InputStream from socket, so that we can use InputStreamReader in BufferedReader to read in the data
      // Received from server.
      sock = new Socket(serverName, port);  // try/catch will handle possible exception this line
      fromServer = new BufferedReader((new InputStreamReader(
          sock.getInputStream())));  //try/catch will handle possible exception this line

      // Sock.getOutputStream() retrieving output stream from socket. This is the data we want to write to server.
      // GetOutStream() -> OutputStream, which has the following methods available for use: flush, close, write
      toServer = new PrintStream(
          sock.getOutputStream());  //try/catch will handle possible exception this line

      if (updatingServerStatus) {
        textFromServer = fromServer.readLine();
        JokeClientAdmin.secondaryServer = Boolean.parseBoolean(textFromServer);
        //  System.out.println("Value in is: " + JokeClientAdmin.secondaryServer);

        updatingServerStatus = false;
      }

      if (updatingMode) {
        //writing name
        toServer.println(mode);

        //flushing stream
        toServer.flush();

        // Read no more than three lines of from the server
        textFromServer = fromServer.readLine();
        for (int i = 1; i <= 2; i++) {
          textFromServer = fromServer.readLine();

          //If string not empty, print
          if (textFromServer != null)

          //  System.out.println("Variable" + secondaryServer);
          {
            System.out.println(textFromServer);
          }
        }

        updatingMode = false;
      }
      //close socket
      sock.close();

    } catch (IOException x) {
      //if exception found print socket error
      System.out.println("Error: Socket error.");
      //trace up stack and print errors related to what caused exception
      x.printStackTrace();
    }
  }

}