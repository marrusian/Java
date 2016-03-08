import java.net.*;
import java.io.*;

public class ChatServer implements Runnable
{
	private ServerSocket serverSocket = null;
	private Thread serverThread = null;
	private ChatServerThread client = null;

	public ChatServer(int portNumber)
	{
		try {
			System.out.println("Binding to port " + portNumber + ". Please wait...");
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Server started: " + serverSocket);
			
			startThread();
		} catch (IOException ioe) {
			System.err.println("Server socket creation error: " + ioe.getMessage());
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
			} 
	}

	public void addThread(Socket clientSocket)
	{
		System.out.println("Client accepted: " + clientSocket);
		client = new ChatServerThread(this, clientSocket);

		try {
			client.open();
			client.start();
		} catch (IOException ioe) {
			System.err.println("Error opening thread: " + ioe);
		}
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

	public ChatServerThread(ChatServer server, Socket clientSocket)
	{
		this.server = server;
		this.clientSocket = clientSocket;
		ID = clientSocket.getPort();
	}

	@Override public void run()
	{
		System.out.println("Server Thread " + ID + " running.");
		String inputLine;

		try {
			while ((inputLine = clientIn.readLine()) != null)
				System.out.println(inputLine);
		} catch (IOException ioe) {
			System.err.printf("Can't get client's %d input [%s]", ID, ioe.getMessage());
			System.err.println();
		} finally {
			closeQuietly();
		}
	}

	public void open() throws IOException
	{
		clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	public void closeQuietly()
	{
		try {
			if (clientSocket != null)
				clientSocket.close();
			if (clientIn != null)
				clientIn.close();
		} catch (IOException ioe) {
			System.err.println("Can't close resources: " + ioe.getMessage());
		}
	}
}