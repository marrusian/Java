import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ChatServer implements Runnable
{
	private static final int MAX_CLIENTS = 50;

	private ArrayList<ChatServerThread> clients = new ArrayList<>();
	private ServerSocket serverSocket = null;
	private Thread serverThread = null;

	public ChatServer(int portNumber)
	{
		try {
			System.out.println("Binding to port " + portNumber + ". Please wait...");
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Server started: " + serverSocket);
			
			startThread();
		} catch (IOException ioe) {
			System.err.println("Cannot bind to port " + portNumber + ": " + ioe.getMessage());
		}
	}

	@Override public void run()
	{
		try {
				Thread thisThread = Thread.currentThread();
				while (thisThread == serverThread) {
					System.out.println("Waiting for a client...");
					addThread(serverSocket.accept());
				}
			} catch (IOException ioe) {
				System.err.println("Acceptance error: " + ioe.getMessage());
			} finally {
				stopThread();
			}
	}

	private int findClient(int ID)
	{
		for (int i = 0, clientsSize = clients.size(); i < clientsSize; ++i)
			if(clients.get(i).getID() == ID)
				return i;
		
		return -1;
	}

	public synchronized String handle(int ID, String input)
	{
		int clientIndex = findClient(ID);
		if (clientIndex != -1) {
			if (input == null) {
				clients.get(clientIndex).send(ID + " left the chat room.");
				remove(ID);
			}
			else {
				for (int i = 0, clientsSize = clients.size(); i < clientsSize; ++i)
					clients.get(i).send(ID + ": " + input);
				return "ok";   // Dummy string
			}
		}
		
		return null;
	}
	
	public synchronized void remove(int ID)
	{
		int clientIndex = findClient(ID);
		if (clientIndex != -1) {
			ChatServerThread toTerminate = clients.get(clientIndex);
			System.out.println("Removing client thread " + ID + " at " + clientIndex);
			clients.remove(clientIndex);

			try {
				toTerminate.close();
			} catch (IOException ioe) {
				System.err.println("Error closing thread: " + ioe);
			} finally {
				toTerminate.interrupt();
			}
		}
	}

	public void addThread(Socket clientSocket)
	{
		if(clients.size() < MAX_CLIENTS) {
			System.out.println("Client accepted: " + clientSocket);
			clients.add(new ChatServerThread(this, clientSocket));

			try {
				int lastClientIndex = clients.size() - 1;
				clients.get(lastClientIndex).open();
				clients.get(lastClientIndex).start();
			} catch (IOException ioe) {
				System.err.println("Error opening thread: " + ioe);
			}
		}
		else
			System.out.println("Client refused: maximum " + clients.size() + " reached.");
	}

	public void startThread()
	{
		if (serverThread == null) {
			serverThread = new Thread(this);
			serverThread.start();
		}
	}

	public void stopThread()
	{
		serverThread = null;
	}

	public static void main(String args[])
	{
		if (args.length != 1) {
			System.err.println("Usage: java ChatServer <port number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);
		ChatServer chatServer = new ChatServer(portNumber);
		
	
	}
}

class ChatServerThread extends Thread
{
	private Socket clientSocket = null;
	private ChatServer server = null;
	private int ID = -1;

	private BufferedReader clientIn = null;
	private PrintWriter clientOut = null;

	public ChatServerThread(ChatServer server, Socket clientSocket)
	{
	//	super();
		this.server = server;
		this.clientSocket = clientSocket;
		ID = clientSocket.getPort();
	}

	@Override public void run()
	{
		System.out.println("Server Thread " + ID + " running.");

		try {
			while (!currentThread().interrupted() && server.handle(ID, clientIn.readLine()) != null)
				continue;
		} catch (IOException ioe) {
			System.err.println(ID + " error reading: " + ioe.getMessage());
		} finally {
			server.remove(ID);
			currentThread().interrupt();
		}
	}

	public int getID() {return ID;}

	public void send(String msg)
	{
		try {
			clientOut.println(msg);
		} finally {
			if (System.out.checkError()) {
				System.err.println(ID + " error sending: " + msg);
				server.remove(ID);
				currentThread().interrupt();
			}
		}
	}

	public void open() throws IOException
	{
		clientIn  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
	}

	public void close() throws IOException
	{
		if (clientSocket != null)
			clientSocket.close();
		if (clientIn != null)
			clientIn.close();
		if (clientOut != null)
			clientOut.close();
	}
}