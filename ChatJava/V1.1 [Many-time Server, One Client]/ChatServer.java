import java.net.*;
import java.io.*;

public class ChatServer implements Runnable
{
	private Socket clientSocket = null;
	private ServerSocket serverSocket = null;
	private BufferedReader clientIn = null;
	private Thread clientThread = null;

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
			while (thisThread == clientThread) {
				System.out.println("Waiting for a client...");
				clientSocket = serverSocket.accept();
				System.out.println("Client accepted: " + clientSocket);

				open();

				String inputLine;
				while ((inputLine = clientIn.readLine()) != null) {
					System.out.println(inputLine);
				}
			}
		} catch (IOException ioe) {
			System.err.println("Acceptance error: " + ioe.getMessage());
		} finally {
			closeQuietly();
		}
	}

	public void startThread()
	{
		if (clientThread == null) {
			clientThread = new Thread(this);
			clientThread.start();
		}
	}

	public void stopThread()
	{
		clientThread = null;
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