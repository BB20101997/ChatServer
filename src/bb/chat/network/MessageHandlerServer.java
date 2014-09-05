package bb.chat.network;

import bb.chat.command.*;
import bb.chat.gui.ChatServerGUI;
import bb.chat.interfaces.IIOHandler;
import bb.chat.interfaces.IMessageHandler;
import bb.chat.interfaces.IPacket;
import bb.chat.network.handler.BasicMessageHandler;
import bb.chat.network.handler.DefaultPacketHandler;
import bb.chat.network.handler.IOHandler;
import bb.chat.network.packet.Chatting.ChatPacket;
import bb.chat.network.packet.Command.RenamePacket;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author BB20101997
 */
public class MessageHandlerServer extends BasicMessageHandler
{
	/**
	 * Static Actor representing the Server�s Help function
	 */
	private final ConnectionListener	conLis;

	public MessageHandlerServer(int port, boolean gui)
	{
		conLis = new ConnectionListener(port, this);
		if(gui)
		{
			new ChatServerGUI(this).setVisible(true);
		}
		new Thread(getConLis()).start();
		localActor = SERVER;
		side = Side.SERVER;


		PD.registerPacketHandler(new DefaultPacketHandler(this));

		addCommand(Help.class);
		addCommand(Rename.class);
		addCommand(Whisper.class);
		addCommand(Disconnect.class);
        addCommand(Stop.class);
	}

	@Override
	public void connect(String host, int port)
	{

		// not used Server-Sided yet ,may in the future
	}

    @Override
    public void receivePackage(IPacket p, IIOHandler sender) {
        //TODO
        if(p instanceof ChatPacket){
            setEmpfaenger(ALL);
            sendPackage(p);
            println(sender.getActorName() + " : " + ((ChatPacket) p).message);
        }
    }

    @Override
    public void sendPackage(IPacket p) {
        if(Target == ALL){
            for(IIOHandler ica: actors){
                    ica.sendPacket(p);
            }
        }
        else{
           if(Target instanceof IOHandler){
               Target.sendPacket(p);
           }
        }
    }

    @Override
	public void disconnect(IIOHandler ica)
	{
        if(ica != ALL) {
            ica.stop();
        }
        else{
               for(IIOHandler a:actors){
                   a.stop();
                   actors.remove(a);
               }
        }
	}

    @Override
    public void shutdown(){
        super.shutdown();
        conLis.end();
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
		final List<Socket>		clientSocketList	= new ArrayList<Socket>();
		final List<Thread>		clientThreadList	= new ArrayList<Thread>();
		final IMessageHandler		MH;

		/**
		 * new ConnectionListener using default port = 256
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
		 * Stop the ConnectionListener
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

			for(IIOHandler ica : actors)
			{

				if(ica instanceof IOHandler)
				{
					IOHandler io = (IOHandler) ica;

					try
					{
						io.stop();
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
			try
			{


                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null,null,null);

                SSLServerSocketFactory ssf = sc.getServerSocketFactory();
                SSLServerSocket socketS = (SSLServerSocket) ssf.createServerSocket(port);

                bb.chat.util.Socket.enableAnonConnection(socketS);

				if(MH != null)
				{
					MH.println("Awaiting connections on " + socketS.getLocalSocketAddress());
				}
				while(continueLoop)
				{
                    SSLSocket s = (SSLSocket) socketS.accept();
					if(!continueLoop)
					{
						s.close();
						break;
					}
					logins++;
					String n = "Anonym-User-" + logins;
					IOHandler c = new IOHandler(s.getInputStream(), s.getOutputStream(), MH);
					c.setActorName(n);
                    c.sendPacket(new RenamePacket("Client", n));

					clientSocketList.add(s);
					actors.add(c);
					clientThreadList.add(new Thread(c));

					clientThreadList.get(clientThreadList.size() - 1).start();

					setEmpfaenger(ALL);
                    sendPackage(new ChatPacket(getActor().getActorName() + " : " + n + " joined the Server",getActor().getActorName()));
					println(getActor().getActorName() + " : " + n + " joined the Server");
					System.out.println("Connection established");
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            for(IIOHandler ica : actors)
			{
				if(ica instanceof IOHandler)
				{
					IOHandler cl = (IOHandler) ica;

					try
					{
						cl.stop();
					}
					catch(Throwable e)
					{
						e.printStackTrace();
					}
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