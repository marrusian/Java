import java.net.*;
import java.io.*;

public class ChatClient
{
	private Socket serverSocket = null;
	private BufferedReader stdIn = null;
	private PrintWriter serverOut = null;

	public ChatClient(String serverName, int serverPort) throws IOException
	{
		try {
			System.out.println("Establishing connection. Please wait...");
			serverSocket = new Socket(serverName, serverPort);
			System.out.println("Connected: " + serverSocket);
			
			start();
			
			String inputLine;
			while ((inputLine = stdIn.readLine()) != null) {
				serverOut.println(inputLine);
			}
		} catch (UnknownHostException uhe) {
			System.err.printf("Don't know about host %s [%s]", serverName, uhe.getMessage());
			System.err.println();
		} catch (IOException ioe) {
			System.err.printf("Couldn't get I/O for the connection to %s [%s]", serverName, ioe.getMessage());
			System.err.println();
		} finally {
			stop();
		}
	}

	public void start() throws IOException
	{
		stdIn = new BufferedReader(new InputStreamReader(System.in));
		serverOut = new PrintWriter(serverSocket.getOutputStream(), true);
	}

	public void stop() throws IOException
	{
		if (stdIn != null)
			stdIn.close();
		if (serverOut != null)
			serverOut.close();
		if (serverSocket != null)
			serverSocket.close();
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