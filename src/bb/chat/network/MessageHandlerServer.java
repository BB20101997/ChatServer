package bb.chat.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import bb.chat.command.Disconnect;
import bb.chat.command.Help;
import bb.chat.command.Rename;
import bb.chat.command.Whisper;
import bb.chat.gui.ChatServerGUI;
import bb.chat.interfaces.IChatActor;
import bb.chat.interfaces.IMessageHandler;

/**
 * @author BB20101997
 */
public class MessageHandlerServer extends BasicMessageHandler implements IMessageHandler
{
	/**
	 * Static Actor representing the Server´s Helpfunction
	 */
	private ConnectionListener	conLis;

	/**
	 * @param CL
	 *            a ConnectionListener to Manage the Connections Server-Side
	 *            Also the Constructor adds the basic Commands
	 */
	public MessageHandlerServer(int port, boolean gui)
	{

		conLis = new ConnectionListener(port, this);
		if(gui)
		{
			new ChatServerGUI(this).setVisible(true);;
		}
		new Thread(getConLis()).start();
		println("Started server on port :" + port);
		localActor = SERVER;
		side = Side.SERVER;
		addCommand(Help.class);
		addCommand(Rename.class);
		addCommand(Whisper.class);
		addCommand(Disconnect.class);
	}

	@Override
	public void connect(String host, int port)
	{

		// not used Server-Sided yet ,may in the futur
	}

	@Override
	public void disconnect(IChatActor ica)
	{

		ica.disconnect();

	}

	@Override
	public void recieveMessage(String message, IChatActor sender)
	{

		System.out.println("Recieved : " + message);
		if(!message.startsWith("/") && !message.equals(""))
		{
			if(sender != null)
			{
				setEmpfaenger(ALL);
				sendMessage(sender.getActorName() + " : " + message, sender);
				println(sender.getActorName() + " : " + message);

			}
			else
			{
				setEmpfaenger(ALL);
				sendMessage(getActor().getActorName() + " : " + message, getActor());
				println("UNKOWN/SERVER" + " : " + message);
			}
		}
		else
		{
			String[] strA = message.split(" ");
			strA[0] = strA[0].replace("/", "");
			getCommand(strA[0]).runCommandRecievedFromClient(message, this, sender);

		}

	}

	@Override
	public void sendMessage(String text, IChatActor Send)
	{

		if(Target == ALL)
		{
			for(IChatActor ica : actors)
			{
				if(ica instanceof IOHandler)
				{
					IOHandler io = (IOHandler) ica;
					io.getOut().println(text);
					io.getOut().flush();
				}
			}
		}
		else
		{
			if(Target instanceof IOHandler)
			{
				IOHandler io = (IOHandler) Target;
				io.getOut().println(text);
				io.getOut().flush();
			}
		}
	}

	// Used by the Server Gui to stop Server on Window closing
	public ConnectionListener getConLis()
	{

		return conLis;
	}

	public class ConnectionListener extends Thread
	{
		private final int	port;
		private int			logins				= 0;
		private boolean		continueLoop		= true;
		List<Socket>		clientSocketList	= new ArrayList<Socket>();
		List<Thread>		clientThreadList	= new ArrayList<Thread>();
		IMessageHandler		MH;

		/**
		 * new ConnectionListener using default port = 200
		 */
		public ConnectionListener(IMessageHandler m)
		{

			MH = m;
			port = 256;
		}

		/**
		 * @param p
		 *            the Port the ConnectionListener will use
		 */
		public ConnectionListener(int p, IMessageHandler m)
		{

			MH = m;
			port = p;
		}

		/**
		 * Stop's the ConnectionListener
		 */
		public void end()
		{

			continueLoop = false;
			interrupt();
			System.out.println("Closing for new Connections");

			Socket s = null;
			try
			{
				s = new Socket("localhost", port);
			}
			catch(UnknownHostException e)
			{
				e.printStackTrace();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(s != null)
				{
					try
					{
						s.close();
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
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

			for(IChatActor ica : actors)
			{

				if(ica instanceof IOHandler)
				{
					IOHandler io = (IOHandler) ica;

					try
					{
						io.finalize();
					}
					catch(Throwable e)
					{
						e.printStackTrace();
					}

				}

				actors.remove(ica);
			}
		}

		@Override
		protected void finalize() throws Throwable
		{

			end();
			super.finalize();
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
					c.getOut().println("/rename " + " Client " + n);

					clientSocketList.add(s);
					actors.add(c);
					clientThreadList.add(new Thread(c));

					clientThreadList.get(clientThreadList.size() - 1).start();;

					setEmpfaenger(ALL);
					sendMessage(getActor().getActorName() + " : " + n + " joind the Server", getActor());
					println(getActor().getActorName() + " : " + n + "joind the Server");
					System.out.println("Connection astablished");
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			for(IChatActor ica : actors)
			{
				if(ica instanceof IOHandler)
				{
					IOHandler cl = (IOHandler) ica;

					try
					{
						cl.finalize();
					}
					catch(Throwable e)
					{
						e.printStackTrace();
					};
				}
			}
			actors.clear();

		}

		@Override
		public void run()
		{

			if((port != -1) && (port >= 0) && (port <= 65535))
			{
				listen();
			}
		}

		public IMessageHandler getMessageHandler()
		{

			return MH;
		}
	}
}