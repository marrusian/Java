import java.net.*;
import java.io.*;

public class ChatClient implements Runnable
{
	private Thread clientThread = null;
	private ChatClientThread client = null;
	private Socket serverSocket = null;
	private BufferedReader stdIn = null;
	private PrintWriter serverOut = null;

	public ChatClient(String serverName, int serverPort)
	{
		try {
			System.out.println("Establishing connection. Please wait...");
			serverSocket = new Socket(serverName, serverPort);
			System.out.println("Connected: " + serverSocket);
			
			start();
		} catch (UnknownHostException uhe) {
			System.err.println("Host unknown: " + uhe.getMessage());
		} catch (IOException ioe) {
			System.err.println("Unexpected exception: " + ioe.getMessage());
		}
	}

	@Override public void run()
	{
		Thread thisThread = Thread.currentThread();
		try {
			while (thisThread == clientThread)
				serverOut.println(stdIn.readLine());
		} catch (IOException ioe) {
			System.err.println("Sending error: " + ioe.getMessage());
		} finally {
			stop();
		}
	}

	public String handle(String msg)
	{
		if (msg != null) {
			System.out.println(msg);
			return "ok"; // Dummy string
		} else {
			System.out.println("Good bye. Press RETURN to exit.");
			stop();
			return null;
		}
	}

	public void start() throws IOException
	{
		stdIn = new BufferedReader(new InputStreamReader(System.in));
		serverOut = new PrintWriter(serverSocket.getOutputStream(), true);
		
		if (clientThread == null) {
			client = new ChatClientThread(this, serverSocket);
			clientThread = new Thread(this);
			clientThread.start();
		}
	}

	public void stop()
	{
		clientThread = null;

		try {
			if (stdIn != null)
				stdIn.close();
			if (serverOut != null)
				serverOut.close();
			if (serverSocket != null)
				serverSocket.close();
		} catch (IOException ioe) {
			System.err.println("Error closing");
		} finally {
			client.close();
			client.interrupt();
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		if (args.length != 2) {
			System.err.println("Usage: java ChatClient <host name> <port number>");
			System.exit(1);
		}
		
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		
		ChatClient client = new ChatClient(hostName, portNumber);
	}
}

class ChatClientThread extends Thread
{
	private Socket serverSocket = null;
	private ChatClient client = null;
	private BufferedReader serverIn = null;
	
	public ChatClientThread(ChatClient client, Socket serverSocket)
	{
		this.client = client;
		this.serverSocket = serverSocket;
		open();
		start();
	}
	
	public void open()
	{
		try {
			serverIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
		} catch (IOException ioe) {
			System.err.println("Error getting input stream: " + ioe);
			client.stop();
		}
	}
	
	public void close()
	{
		try {
			if (serverIn != null)
				serverIn.close();
		} catch (IOException ioe) {
			System.err.println("Error closing input stream: " + ioe.getMessage());
		}
	}

	@Override public void run()
	{
		try {
			while (client.handle(serverIn.readLine()) != null)
				continue;
		} catch (IOException ioe) {
			System.err.println("Listening error: " + ioe.getMessage());
			client.stop();
		}
	}
}