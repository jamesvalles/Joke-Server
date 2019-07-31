/*  This file is JokeServer for the Joke Server project for Distributed System I

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


import java.io.*; // Used to retrieve the I/O libraries
import java.net.*; // Used to retrieve the Java networking libraries
import java.util.HashMap; // Used to store jokes
import java.util.Random; // Used to generate random integer


// This is the JokeServer Class
public class JokeServer {

  // Initializing static varibales to be used throughout all classes. I could do a better job handling this in the future.
  static String mode = "Joke";
  static String name;
  static String id = "";
  static int[] jokesSeen;
  static int[] proverbsSeen;
  static ClientRecord inClient;
  static Boolean secondarySever = false;

  // Initializing HashMaps to store jokes, provers, and client data, so that it can retrieve state
  static HashMap<String, String> jokes = new HashMap<>();
  static HashMap<String, String> proverbs = new HashMap<>();
  static HashMap<String, ClientRecord> clientData = new HashMap<>();

  // will throw an IOException because we will be reading and writing while in main


  // This method will return a joke with a given key JA, JB, JC, JD.
  // I did not write these jokes or proverbs they came from the internet.
  public static String getJoke(String key) {
    jokes.put("JA",
        "How did the programmer die in the shower? He read the shampoo bottle instructions: Lather. Rinse. Repeat.");
    jokes.put("JB",
        "Why do programmers always mix up Halloween and Christmas? Because Oct 31 equals Dec 25.");
    jokes.put("JC",
        "How many programmers does it take to change a light bulb? None – It’s a hardware problem.");
    jokes.put("JD", "Why do Java programmers wear glasses? They can't C#.");

    // If this is the secondary server, this is the string formatting you will output to screen. This includes <S2, the jokes key, user's name, and joke.
    // Otherwise in else black return string formatting for primary server.
    if (JokeServer.secondarySever) {
      return "<S2> " + key + " " + name + ": " + jokes.get(key);
    } else {
      return key + " " + name + ": " + jokes.get(key);
    }
  }

  // This method will return a joke with a given key PA, PB, PC, PD.
  // I did not write these jokes or proverbs they came from the internet.
  public static String getProverb(String key) {
    proverbs.put("PA", "Early to bed and early to rise, makes a man healthy, wealthy and wise.");
    proverbs.put("PB", "It's no use locking the stable door after the horse has bolted.");
    proverbs.put("PC", "Laugh and the world laughs with you, weep and you weep alone.");
    proverbs.put("PD",
        "See a pin and pick it up, all the day you'll have good luck; see a pin and let it lie, bad luck you'll have all day.");

    // If this is the secondary server, this is the string formatting you will output to screen. This includes <S2, the jokes key, user's name, and joke.
    // Otherwise in else black return string formatting for primary server.
    if (JokeServer.secondarySever) {
      return "<S2> " + key + " " + name + ": " + proverbs.get(key);
    } else {
      return key + " " + name + ": " + proverbs.get(key);
    }
  }

  // This is the main method.
  public static void main(String[] args) throws IOException {

    String mode = "Joke"; // Initialize the program to start in Joke mode .
    Socket sock; // Create socket using imported java networking library
    int q_len = 6; // Used as a setting needed for a socket, tells OS if we have six simultaneous
    // Connections, keep first 6 and discard the rest, queue of requests os should process
    int port = 0; // This is the port number, will ask OS to use this port, initialized to 0 at start.

    System.out.println("James Valles' Joke Server running.");

    //If there are no args entered at command line (indicating primary server only, set port to 4545, and print this message)
    if (args.length < 1) {
      port = 4545;
      System.out
          .println("Server one: localhost, port " + port + ". JokeClientAdmin using port 5050.");
    } else {

      //If 'secondary' entered at command line, (indicating secondary server, set port to 4546, and print this message)
      port = 4546;
      System.out
          .println("Server two: localhost, port " + port + ". JokeClientAdmin using port 5051.");

      //Set secondary server variable to true. This will be used to flip port numbers, in server script if secondary server is initialized.
      secondarySever = true;
    }

    //Print to screen the mode JokeServer is starting in.
    System.out.println(
        ("\nStarted in "
            + JokeServer.mode
            + " mode."));

    //Establish a new JokeClientAdmin connection
    AdminclientConnection adminclientConnection = new AdminclientConnection();

    //Create new thread to run this process pass it AdminClientConnection object.
    Thread adminClientThread = new Thread(adminclientConnection);

    //Start the thread
    adminClientThread.start();

    //Create a new server socket (door bell that will listen on either 4545 or 4546.
    ServerSocket serverSock = new ServerSocket(port, q_len);

    //Loop while true, execute this block of code.
    while (true) {
      //Initially the server will have a server socket and not a socket, it will wait for incoming client connection requests
      // Once a request comes in it will accept it, a socket object is then created on the server side.
      sock = serverSock.accept(); // Saves the return value of accepting new connection from client

      //Create a new worker thread (multi-threaded server) and pass it a socket, then start it
      new Worker(sock).start();

    }
  }
}

// This class will keep track of state, ie. user information, in this case list of seen jokes/proverbs, clients name, and id.
class ClientRecord {

  // Client record variables initialized. seenJokes and seenProverbs arrays will keep track of which jokes have been seen 0 - not seen 1 - seen
  String name;
  String id;
  int[] seenJokes = {0, 0, 0, 0};
  int[] seenProverbs = {0, 0, 0, 0};

  // Getters and setters to be used with this Client Record objects
  public String getName() {
    return name;
  }

  public String getId() {
    return id;
  }

  public int[] getSeenJokes() {
    return seenJokes;
  }

  public int[] getSeenProverbs() {
    return seenProverbs;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setSeenJokes(int[] seenJokes) {
    this.seenJokes = seenJokes;
  }

  public void setSeenProverbs(int[] seenProverbs) {
    this.seenProverbs = seenProverbs;
  }
}

//JokeClientAdmin connection which will create a new connection on port 5050 if no secondary server so that JokeClientAdmin can communicate with JokeServers.
class AdminclientConnection implements Runnable {

  @Override
  //This class will establish connection between sever and client admin to toggle between joke and proverb mode and to switch servers and shut them down (couldn't get this far :( )

  public void run() {
    int port;
    int q_len = 6; // Used as a setting needed for a socket, tells OS if we have six simultaneous
    // Connections, keep first 6 and discard the rest, queue of requests os should process

    //If this is a secondary server, you will communicate with AdminClient on port 5051
    if (JokeServer.secondarySever) {
      port = 5051;
    } else {
      //If this is a primary server, you will communicate with AdminClient on port 5050
      port = 5050;
    }
    // This is the port number, will ask OS to use this port
    Socket sock; //Create socket using imported java networking library

    // Create a new server socket using q_length and port. server socket is listening at port 5050/5051.
    try {
      ServerSocket serverSock = new ServerSocket(port, q_len);
      //System.out.println(("James Valles' Client Admin Server starting up, listening at port 5050.\n"));

      while (true) { // Loop while true, execute this block of code.
        // Initially the server will have a serversocket and not a socket, it will wait for an incoming client connection requests
        // Once a request comes in it will accepted, a socket object is created on the server side.
        sock = serverSock.accept(); //Saves the return value of accepting new connection from client

        //Create a new worker thread (multi-threaded server) and pass it a socket, then start it
        new ClientAdminWorker(sock).start();
      }
      //This loop happens over and over again while true
    } catch (IOException exception) {
      System.out.println(exception);

    }
  }
}

//JokeClientAdmin Worker thread
class ClientAdminWorker extends Thread {


  Socket sock; // Socket is a class from java.net. Socket is an endpoint.  (java object). Used for communication.

  ClientAdminWorker(
      Socket s) // This is a constructor for the Worker class, where a Socket is passed in assigned to sock.
  {
    sock = s;
  }

  // This method will be used to run some code. to have it run on a seperate thread must not call directly, but should use .start() .stop(), etc.
  public void run() {
    //clientIn this case run() will run the process for retrieving the clientIn and clientOut streams from the socket (end point of communication).
    // output stream using for sending data, input stream using for reading data.

    PrintStream clientOut = null; //Initializing printstream so that we can write (send) data, flushes automatically
    BufferedReader clientIn = null; //Initializing bufferedreader so that we can read clientIn data

    try {
      //Creating a new bufferedreader (character input stream -> text. reads characters stores clientIn internal buffer to reduce i/o operations) using an InputStreamReader (reads bytes and decodes them into characters)
      clientIn = new BufferedReader(new InputStreamReader(
          sock.getInputStream())); //Getting unhandled exception here, so need to implement exception handling
      clientOut = new PrintStream((sock
          .getOutputStream())); //Getting unhandled exception here, so need to implement exception handling
      try {

        //This will send clientAdmin boolean value whether or not secondarySever connected
        clientOut.println(JokeServer.secondarySever);
        JokeServer.name = clientIn
            .readLine(); //Reading the first line of text the socket received using InputStreamReader and BufferedReader

        //If in joke mode, switch to proverb, this handles joke/proverb toggling.
        if (JokeServer.mode.equals("Joke")) {
          JokeServer.mode = "Proverb";
        } else {
          JokeServer.mode = "Joke";
        }

        // Mode prints to screen.
        String stateNotice = "Switched mode, currently in "
            + JokeServer.mode + " mode.";

        System.out.println(stateNotice); //simply print to screen current mode
        clientOut.println(stateNotice);


      } catch (IOException x) {
        //If exception raised if there is no line to readin, then print "Server read error
        System.out.println("Server read error");

        // Invoke function PrintStrackTrace on IOException object x to go up the stack and print where exception occurred. This will help developer understand where exception was raised
        x.printStackTrace();
      }
      sock.close(); // once we are done using the socket, we can invoke method to close it
    } catch (
        IOException e) {
      // This will help catch possible exceptions raised with getInputStream and OutputStream
      System.out.println(e);
    }
  }
}

class Worker extends Thread {


  Socket sock; //socket is a class from java.net. Socket is an endpoint.  (java object). Used for communication.

  Worker(
      Socket s) //this is a constructor for the Worker class, where a Socket is passed in assigned to sock.
  {
    sock = s;
  }

  //this method will be used to run some code. to have it run on a seperate thread must not call directly, but should use .start() .stop(), etc.
  public void run() {
    //in this case run() will run the process for retrieving the in and out streams from the socket (end point of communication).
    // output stream using for sending data, input stream using for reading data.

    PrintStream out = null; //initializing printstream so that we can write (send) data, flushes automatically
    BufferedReader in = null; //initializing bufferedreader so that we can read in data

    try {
      //creating a new bufferedreader (character input stream -> text. reads characters stores in internal buffer to reduce i/o operations) using an InputStreamReader (reads bytes and decodes them into characters)

      //sock.getInputStream() retrieving InputStream from socket, so that we can use InputStreamReader in BufferedReader to read in the data received from socket
      //getInputStream() -> InputStream, which has methods such as read, close, skip, reset, mark, etc.
      in = new BufferedReader(new InputStreamReader(
          sock.getInputStream())); //getting unhandled exception here, so need to implement exception handling

      //sock.getOutputStream() retrieving outpit stream from socket. This is the data we want to write.
      //getOutStream() -> OutputStream, which has the following methods available for use: flush, close, write
      out = new PrintStream((sock
          .getOutputStream())); //getting unhandled exception here, so need to implement exception handling
      try {
        // String name; //initialize variable that will store the web address that was entered captured by INetClient, this will be the domain name that is searched.

        JokeServer.name = in
            .readLine(); //reading the first line of text the socket received using InputStreamReader and BufferedReader
        System.out.println(
            "Getting your " + JokeServer.mode + ", "
                + JokeServer.name + "!"); //simply print to screen what server is looking up

        //Reads in id from Client
        JokeServer.id = in.readLine();

        //If client id in client Hashtable, then retrieve, otherwise create a new client record
        if (JokeServer.clientData.containsKey(JokeServer.id)) {
          JokeServer.inClient = JokeServer.clientData.get(JokeServer.id);
          JokeServer.id = JokeServer.clientData.get(JokeServer.id).id;
          JokeServer.name = JokeServer.clientData.get(JokeServer.id).name;
          JokeServer.jokesSeen = JokeServer.clientData.get(JokeServer.id).seenJokes;
          JokeServer.proverbsSeen = JokeServer.clientData.get(JokeServer.id).seenProverbs;


        } else {
          ClientRecord user = new ClientRecord();
          user.setName(JokeServer.name);
          user.setId(JokeServer.id);
          JokeServer.clientData.put(JokeServer.id, user);
          JokeServer.inClient = JokeServer.clientData.get(JokeServer.id);
        }

        //send name to client
        outToClient(JokeServer.name,
            out); //invokes outToClient function that will retrieve Host Name, Host IP, or throw an exception if name cannot be found.
        //Method requires name (String) and out (PrintStream) as parameters

      } catch (IOException x) {
        //if exception raised if there is no line to readin, then print "Server read error
        System.out.println("Server read error");

        //invoke function PrintStrackTrace on IOException object x to go up the stack and print where exception occurred. This will help developer understand where exception was raised
        x.printStackTrace();
      }
      sock.close(); // once we are done using the socket, we can invoke method to close it
    } catch (
        IOException ioe) {
      //this will help catch possible exceptions raised with getInputStream and OutputStream
      System.out.println(ioe);
    }


  }

  //Method Sends jokes to client
  public void outToClient(String name, PrintStream out) {
    Message message = decideNextJoke(JokeServer.inClient);

    //If in joke mode, then get a joke, and keep track of joke cycles , else get a proverb and keep track of proverb cycles.
    if (JokeServer.mode.equals("Joke")) {

      if (message.getJokeCycle() != null) {
        out.println(message.getJokeCycle());
      }
      out.println(JokeServer.getJoke(message.nextJoke));

      //   out.println("Id: " + JokeServer.id);
      // out.println(JokeServer.clientData.size());

    } else {
      //  Message message = decideNextJoke(JokeServer.inClient);
      if (message.getProverbCycle() != null) {
        out.println(message.getProverbCycle());
      }
      out.println(JokeServer.getProverb(message.nextJoke));

      //   out.println(JokeServer.getProverb(decideNextJoke(JokeServer.inClient)));
    }
  }

  //This method decides which joke is next. It will randomly check for a given joke or proverb, if already seen, it will iterate through arrays,
  //and pick the next one that hasn't been seen and mark it. When all jokes seen, it will print CYCLE message to screen, and reset arrays to all not seen.

  public Message decideNextJoke(ClientRecord inClient) {
    int nextIndex = 0;
    String nextJoke = "";
    HashMap<String, String> jokeDict = new HashMap<>();
    HashMap<String, String> proverbDict = new HashMap<>();

    jokeDict.put("0", "JA");
    jokeDict.put("1", "JB");
    jokeDict.put("2", "JC");
    jokeDict.put("3", "JD");

    proverbDict.put("0", "PA");
    proverbDict.put("1", "PB");
    proverbDict.put("2", "PC");
    proverbDict.put("3", "PD");

    Message message = new Message();
    Random randomNumber = new Random();
    int randomIndex = randomNumber.nextInt((3 - 0) + 1) + 0;

    if (JokeServer.mode.equals("Joke")) {
      boolean seenAllJokes = true;
      int[] seenJokes = inClient.getSeenJokes();

      for (int i = 0; i < seenJokes.length; i++) {
        if (seenJokes[i] != 1) {
          seenAllJokes = false;
          break;
        }
      }

      if (seenAllJokes) {

        System.out.println("\nJOKE CYCLE COMPLETED\n");
        seenJokes[0] = 0;
        seenJokes[1] = 0;
        seenJokes[2] = 0;
        seenJokes[3] = 0;
        message.setJokeCycle("JOKE CYCLE COMPLETED\n");
      }

      if (seenJokes[randomIndex] == 0 && seenAllJokes) {
        seenJokes[randomIndex] = 1;
        inClient.setSeenJokes(seenJokes);
        nextJoke = jokeDict.get(String.valueOf(randomIndex));
        message.nextJoke = nextJoke;
        return message;
      } else {

        //j  System.out.println(Arrays.toString(seenJokes));

        for (int i = 0; i < seenJokes.length; i++) {
          if (seenJokes[i] == 0) {
            seenJokes[i] = 1;
            inClient.setSeenJokes(seenJokes);
            nextJoke = jokeDict.get(String.valueOf(i));
            message.nextJoke = nextJoke;
            break;
          }

        }
        return message;
      }
    } else {

      boolean seenAllProverbs = true;
      int[] seenProverbs = inClient.getSeenProverbs();

      for (int i = 0; i < seenProverbs.length; i++) {
        if (seenProverbs[i] != 1) {
          seenAllProverbs = false;
          break;
        }
      }

      if (seenAllProverbs) {
        System.out.println("\nPROVERB CYCLE COMPLETED\n");
        seenProverbs[0] = 0;
        seenProverbs[1] = 0;
        seenProverbs[2] = 0;
        seenProverbs[3] = 0;
        message.setProverbCycle("PROVERB CYCLE COMPLETED\n");
      }

      if (seenProverbs[randomIndex] == 0 && seenAllProverbs) {
        seenProverbs[randomIndex] = 1;
        inClient.setSeenJokes(seenProverbs);
        nextJoke = proverbDict.get(String.valueOf(randomIndex));
        message.nextJoke = nextJoke;
        return message;
      } else {

        //j  System.out.println(Arrays.toString(seenJokes));

        for (int i = 0; i < seenProverbs.length; i++) {
          if (seenProverbs[i] == 0) {
            seenProverbs[i] = 1;
            inClient.setSeenProverbs(seenProverbs);
            nextJoke = proverbDict.get(String.valueOf(i));
            message.nextJoke = nextJoke;
            break;
          }
        }
      }
      return message;
    }
  }

  //Used to transfer data
  class Message {

    String jokeCycle;
    String proverbCycle;
    String nextJoke;

    public String getNextJoke() {
      return nextJoke;
    }

    public void setNextJoke(String nextJoke) {
      this.nextJoke = nextJoke;
    }

    public String getJokeCycle() {
      return jokeCycle;
    }

    public void setJokeCycle(String jokeCycle) {
      this.jokeCycle = jokeCycle;
    }

    public String getProverbCycle() {
      return proverbCycle;
    }

    public void setProverbCycle(String proverbCycle) {
      this.proverbCycle = proverbCycle;
    }
  }
}








