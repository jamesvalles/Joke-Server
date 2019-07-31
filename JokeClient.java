/*  This file is JokeClient for the Joke Server project for Distributed System I

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

//This is the JokeClient Class
public class JokeClient {

  static String name;
  static int port = 4545;
  static boolean updatingMode = false;
  static String serverName2;
  static String serverName = "localhost";

  public static void main(String[] args) {
    Boolean switchserver = true;
    //initialize String which will contain server name

    //set port server will listen on

    //this block of code sets server name
    System.out.println("James Valles' Joke Client");
    if (args.length < 1) {
      serverName = "localhost";
      // port = 5050;
      System.out.println("Server one: localhost, port 4545. JokeClientAdmin running at port 5050.");
    } else {
      System.out.println("Server one: localhost, port 4545. JokeClientAdmin running at port 5050.");
      if (args.length == 2) {
        serverName2 = args[1];
        //port = 5051;
        System.out.println(
            "Server two: " + serverName2 + ", port 4546. JokeClientAdmin running at port 5051.");
      }
    }

    //this prints to the screen at start of program


    //initializing bufferedreader so that we can read in data, we will also use input stream reader here,
    //InputStreamReader (reads bytes and decodes them into characters)
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    //begin try to address exception handling
    try {
      //initialize new String variable name which will contain the hostname a user enters at prompt
      String name;
      String input;
      int id = 0;

      //while look begins
      //user is greeted with 'Enter a hostname or an IP address, quit to end:' while user doesn't
      //enter exit

      System.out.println("\nHi! Please enter your name to begin program.");
      //write any remaining data stored in the out buffer
      System.out.flush();

      name = in.readLine();
      id = 0 + (int) (Math.random() * ((1000000 - 0) + 1));
      System.out.println(
          "\nTo receive something always press return key, to toggle server type 's', or type 'quit' to exit program.");
      do {

        //write any remaining data stored in the out buffer
        System.out.flush();

        input = in.readLine();

        if (input.indexOf("quit") < 0) {
          if (input.equals("s")) {
            if (switchserver) {
              switchserver = false;
              JokeClient.port = 4546;
              System.out.println("Now communicating with localhost, port 4546.");
              severConnection(name, serverName2, id, 4546);

            } else {
              switchserver = true;
              JokeClient.port = 4545;
              System.out.println("Now communicating with localhost, port 4545.");
              severConnection(name, serverName, id, 4545);

            }

          } else {
            updatingMode = true;
            severConnection(name, serverName, id, JokeClient.port);
            // System.out.println("Port is:" + port);

          }
        }
      }
      while (input.indexOf("quit") < 0);
      //exit the program if 'quit' is entered
      System.out.println("User terminated program.");
    } catch (IOException x) {
      //invoke function PrintStrackTrace on IOException object x to go up the stack and print where exception occurred. This will help developer understand where exception was raised
      x.printStackTrace();
    }
  }

  //not sure why this is needed as it isn't use in client, but was included in .pdf (maybe this can be removed?)
  //function that converts ip address to String

//this function retrieves remote address

  static void severConnection(String name, String serverName, int id, int port) {
    Socket sock; //initialize a new socket . Socket is an endpoint.  (java object). Used for communication.
    BufferedReader fromServer; //initializing bufferedreader so that we can read in data from server
    PrintStream toServer; //initializing printstream so that we can write (send) data to server, flushes automatically
    String textFromServer; //initializing new String variable which will hold text from server

    try {
      //create a new socket, using servername, and port 1565 , store in sock variable
      //not sure why port is hardcoded here
      sock = new Socket(serverName, port);  // try/catch will handle possible exception this line

      //sock.getInputStream() retrieving InputStream from socket, so that we can use InputStreamReader in BufferedReader to read in the data
      // received from server.
      fromServer = new BufferedReader(
          (new InputStreamReader(
              sock.getInputStream())));  //try/catch will handle possible exception this line

      //sock.getOutputStream() retrieving output stream from socket. This is the data we want to write to server.
      //getOutStream() -> OutputStream, which has the following methods available for use: flush, close, write
      toServer = new PrintStream(
          sock.getOutputStream());  //try/catch will handle possible exception this line

      //writing name
      toServer.println(name);
      toServer.println(id);

      //flushing stream
      toServer.flush();

      // Read no more than three lines ofrom the server
      for (int i = 1; i <= 3; i++) {
        textFromServer = fromServer.readLine();

        //if string not empty, print
        if (textFromServer != null) {
          System.out.println(textFromServer);
        }
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