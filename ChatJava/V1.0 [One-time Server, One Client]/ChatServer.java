import java.net.*;
import java.io.*;

public class ChatServer
{
	private Socket clientSocket = null;
	private ServerSocket serverSocket = null;
	private BufferedReader clientIn = null;
	
	public ChatServer(int portNumber) throws IOException
	{
		try {
			System.out.println("Binding to port " + portNumber + ". Please wait...");
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Server started: " + serverSocket);
			
			System.out.println("Waiting for a client...");
			clientSocket = serverSocket.accept();
			System.out.println("Client accepted: " + clientSocket);

			open();

			String inputLine;
			while ((inputLine = clientIn.readLine()) != null) {
				System.out.println(inputLine);
			}
		} catch (IOException ioe) {
			System.err.println(ioe);
		} finally {
			close();
		}
	}

	public void open() throws IOException
	{ 
		clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	public void close() throws IOException
	{
		if (clientSocket != null)
			clientSocket.close();
		if (clientIn != null)
			clientIn.close();
	}
	
	public static void main(String args[]) throws IOException
	{
		if (args.length != 1) {
			System.err.println("Usage: java ChatServer <port number>");
			System.exit(1);
		}
		
		int portNumber = Integer.parseInt(args[0]);
		ChatServer chatServer = new ChatServer(portNumber);
	}
}