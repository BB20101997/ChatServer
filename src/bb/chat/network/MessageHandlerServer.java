package bb.chat.network;

import java.util.Arrays;
import java.util.HashMap;

import bb.chat.command.Disconnect;
import bb.chat.command.Help;
import bb.chat.command.Logout;
import bb.chat.command.Rename;
import bb.chat.command.Whisper;
import bb.chat.gui.BasicChatPanel;
import bb.chat.gui.ServerActor;
import bb.chat.interfaces.IChatActor;
import bb.chat.interfaces.ICommand;
import bb.chat.interfaces.IMessageHandler;

/**
 * @author BB20101997
 */
@SuppressWarnings("deprecation")
public class MessageHandlerServer implements IMessageHandler
{

	/**
	 * Static Actor representing the Server
	 */
	public static final IChatActor		SA			= new ServerActor("Server");

	/**
	 * Static Actor representing the Server´s Helpfunction
	 */
	public static final IChatActor		HA			= new ServerActor("Help");

	private ConnectionListener			conLis;

	private HashMap<String, ICommand>	CommandMap	= new HashMap<String, ICommand>();

	/**
	 * @param CL
	 *            a ConnectionListener to Manage the Connections Server-Side
	 *            Also the Constructor adds the basic Commands
	 */
	public MessageHandlerServer(ConnectionListener CL)
	{

		conLis = CL;
		addCommand(Help.class);
		addCommand(Logout.class);
		addCommand(Rename.class);
		addCommand(Whisper.class);
		addCommand(Disconnect.class);
	}

	@Override
	public void addBasicChatPanel(BasicChatPanel BCP)
	{

		// TODO Auto-generated method stub

	}

	@Override
	public void addCommand(Class<? extends ICommand> c)
	{

		try
		{
			ICommand com = c.newInstance();
			CommandMap.put(com.getName(), com);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void connect(String host, int port)
	{

		// not used Server-Sided yet ,may in the futur
	}

	@Override
	public void disconnect(IChatActor ica)
	{

		if(ica == null)
		{
			for(IOHandler cl : conLis.clientIOList)
			{
				cl.end();
			}
			return;
		}

		if(ica instanceof IOHandler)
		{
			IOHandler CL = (IOHandler) ica;
			CL.end();
		}

	}

	@Override
	public ICommand getCommand(String text)
	{

		String[] name = text.split(" ", 2);
		if(CommandMap.containsKey(name[0].replace("/", ""))) { return CommandMap.get(name[0].replace("/", ""));

		}
		return null;
	}

	@Override
	public void help(ICommand ic, IChatActor sender)
	{

		// TODO Auto-generated method stub

	}

	@Override
	public void help(String s, IChatActor sender)
	{

		String[] help = CommandMap.get(s).helpCommand();
		if(help != null)
		{
			for(int i = 0; i < help.length; i++)
			{
				if(!sender.equals("Server"))
				{
					conLis.send(help[i], HA, sender.getActorName());
				}
				else
				{
					if(conLis.chatServerGUI != null)
					{
						conLis.chatServerGUI.println(help[i]);
					}
				}
			}

		}
		else
		{}
	}

	@Override
	public void help(String[] s, IChatActor sender)
	{

		Arrays.sort(s);
		System.out.println(s.length);
		for(int i = 0; i < s.length; i++)
		{
			System.out.println("Sending help");
			help(s[i], sender);
		}
	}

	@Override
	public void helpAll(IChatActor s)
	{

		Object[] a = CommandMap.values().toArray();
		System.out.println(a.length);
		String[] names = new String[a.length];
		for(int i = 0; i < a.length; i++)
		{
			try
			{
				names[i] = ((ICommand) a[i]).getName();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		Arrays.sort(names);
		help(names, s);
	}

	@Override
	public void Message(String message, IChatActor sender)
	{

		System.out.println(message);
		if(!message.startsWith("/") && !message.equals(""))
		{
			if(sender != null)
			{
				conLis.sendAll(sender.getActorName() + " : " + message, sender);
				conLis.chatServerGUI.println(sender.getActorName() + " : " + message);
			}
			else
			{
				conLis.sendAll(message, getActor());
			}
		}
		else
		{
			getCommand(message).runCommandServer(message, this, sender);

		}
	}

	@Override
	public void print(String s)
	{

		System.out.println(s);
		/*
		 * for(BasicChatPanel bcp:BCPList){ bcp.print(s); }
		 */
	}

	@Override
	public void println(String s)
	{

		System.out.println(s);
		/*
		 * for(BasicChatPanel bcp:BCPList){ bcp.println(s); }
		 */
	}

	@Override
	public void recieveMessage(String message, IChatActor sender)
	{

		System.out.println(message);
		if(!message.startsWith("/") && !message.equals(""))
		{
			if(sender != null)
			{
				conLis.sendAll(sender.getActorName() + " : " + message, sender);
				conLis.chatServerGUI.println(sender.getActorName() + " : " + message);
			}
			else
			{
				conLis.sendAll(message, getActor());
			}
		}
		else
		{
			getCommand(message).runCommandRecievedFromClient(message, this, sender);

		}

	}

	@Override
	public void sendMessage(String text, String Empf, IChatActor Send)
	{

		conLis.send(text, Send, Empf);
	}

	@Override
	public void sendMessageAll(String text, IChatActor Send)
	{

		conLis.sendAll(text, Send);
	}

	@Override
	public IChatActor getActor()
	{

		return SA;
	}
}