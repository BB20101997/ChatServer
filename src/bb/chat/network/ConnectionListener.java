package bb.chat.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import bb.chat.gui.ChatServerGUI;
import bb.chat.interfaces.IChatActor;
import bb.chat.interfaces.IMessageHandler;

/**
 * @author BB20101997
 */
public class ConnectionListener extends Thread
{

	private final int		port;
	private int				logins				= 0;
	private boolean			continueLoop		= true;
	List<IOHandler>			clientIOList		= new ArrayList<IOHandler>();
	List<Socket>			clientSocketList	= new ArrayList<Socket>();
	List<Thread>			clientThreadList	= new ArrayList<Thread>();
	ChatServerGUI			chatServerGUI		= null;
	/**
	 * the IMessageHandler the Listener is linked to
	 */
	public IMessageHandler	MH					= new MessageHandlerServer(this);

	/**
	 * new ConnectionListener using default port = 200
	 */
	public ConnectionListener()
	{

		port = 200;
	}

	/**
	 * @param p
	 *            the Port the ConnectionListener will use
	 */
	public ConnectionListener(int p)
	{

		port = p;
	}

	/**
	 * checks all connection if they are still active ,if not they will be
	 * discarded
	 */
	public void checkStillRunning()
	{

		for(int i = 0; i < clientIOList.size(); i++)
		{
			if(!clientIOList.get(i).hasNotStopped())
			{
				clientIOList.remove(i);
				try
				{
					clientSocketList.get(i).close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				clientSocketList.remove(i);
			}
		}
	}

	/**
	 * Stopps the ConnectionListener
	 */
	public void end()
	{

		continueLoop = false;
		interrupt();
		System.out.println("Closing for new Connections");
		try
		{
			new Socket("localhost", port);
		}
		catch(UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		for(Socket cl : clientSocketList)
		{
			try
			{
				cl.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		for(IOHandler io : clientIOList)
		{
			io.end();
		}
	}

	@Override
	protected void finalize() throws Throwable
	{

		end();
		super.finalize();
	}

	/**
	 * @param s
	 *            a String from an IChatActors name
	 * @return an IChatActor Instance which name == s
	 */
	public IChatActor getClientByName(String s)
	{

		if(s.equals(MH.getActor().getActorName())) { return MH.getActor(); }
		for(IOHandler cli : clientIOList)
		{
			if(cli.getActorName().equals(s)) { return cli; }
		}
		return null;
	}

	/**
	 * the main Function called by the run Function ,it is listening for new
	 * Connections
	 */
	public void listen()
	{

		ServerSocket socketS = null;
		try
		{
			socketS = new ServerSocket(port);
			while(continueLoop)
			{
				Socket s = socketS.accept();
				if(!continueLoop)
				{
					s.close();
					break;
				}
				logins++;
				String n = "Anonym-User-" + logins;
				IOHandler c = new IOHandler(s.getInputStream(), s.getOutputStream(), MH);
				c.setActorName(n);
				c.getOut().println("/rename " + n);

				clientSocketList.add(s);
				clientIOList.add(c);
				clientThreadList.add(new Thread(c));

				clientThreadList.get(clientThreadList.size() - 1).start();;

				MH.recieveMessage(n + " joind the Server", MH.getActor());
				System.out.println("Connection astablished");
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		for(IOHandler cl : clientIOList)
		{
			cl.end();
		}
		clientIOList.clear();

	}

	/**
	 * @param cSG
	 *            to Register a new GUI type ChatServerGUI
	 */
	public void registerGUI(ChatServerGUI cSG)
	{

		chatServerGUI = cSG;
		cSG.println("Server started on Port : " + port);
		cSG.setVisible(true);
	}

	@Override
	public void run()
	{

		if((port != -1) && (port >= 0) && (port <= 65535))
		{
			listen();
		}
	}

	/**
	 * @param text
	 *            the Message to be send
	 * @param Sender
	 *            the IChatActor who send the message
	 * @param Empf
	 *            the Receiver
	 */
	public void send(String text, IChatActor Sender, String Empf)
	{

		checkStillRunning();
		System.out.println("Sending : " + text + "\n From : " + Sender.getActorName() + "\n To : "
				+ Empf);
		if(chatServerGUI != null)
		{
			chatServerGUI.println(Sender + " to " + Empf + " : " + text);
		}
		for(IOHandler s : clientIOList)
		{
			if(s.getActorName().equals(Empf))
			{
				s.getOut().println(text);
				s.getOut().flush();
				break;
			}
		}
	}

	/**
	 * @param text
	 *            the text to be send to all Connections
	 * @param ica
	 *            the IChatActor sending the text
	 */
	public void sendAll(String text, IChatActor ica)
	{

		checkStillRunning();
		System.out.println("Sending : " + text + " \nFrom : " + ica.getActorName() + "\nTo : All");
		for(IOHandler s : clientIOList)
		{
			s.getOut().println(text);
			s.getOut().flush();
		}

	}

}
