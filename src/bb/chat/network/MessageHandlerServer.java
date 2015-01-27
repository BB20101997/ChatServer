package bb.chat.network;

import bb.chat.command.*;
import bb.chat.enums.NetworkState;
import bb.chat.enums.ServerStatus;
import bb.chat.enums.Side;
import bb.chat.gui.ChatServerGUI;
import bb.chat.interfaces.IIOHandler;
import bb.chat.interfaces.IMessageHandler;
import bb.chat.interfaces.IPacket;
import bb.chat.network.handler.BasicIOHandler;
import bb.chat.network.handler.BasicMessageHandler;
import bb.chat.network.handler.DefaultPacketHandler;
import bb.chat.network.packet.Command.DisconnectPacket;
import bb.chat.network.packet.Command.RenamePacket;
import bb.chat.security.BasicPermissionRegistrie;
import com.sun.istack.internal.NotNull;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author BB20101997
 */
public class MessageHandlerServer extends BasicMessageHandler {
	/**
	 * Static Actor representing the Server�s Help function
	 */
	private final ConnectionListener conLis;

	@SuppressWarnings("unchecked")
	public MessageHandlerServer(int port, boolean gui) {
		super();
		conLis = new ConnectionListener(port, this);
		if(gui) {
			new ChatServerGUI(this).setVisible(true);
		}
		new Thread(getConLis()).start();
		localActor = SERVER;
		side = Side.SERVER;
		permReg = new BasicPermissionRegistrie();
		PD.registerPacketHandler(new DefaultPacketHandler(this));

		load();
		addCommand(Help.class);
		addCommand(Rename.class);
		addCommand(Whisper.class);
		addCommand(Disconnect.class);
		addCommand(Stop.class);
		addCommand(Save.class);

		serverStatus = ServerStatus.EMPTY;
	}

	@Override
	public boolean connect(String host, int port) {
		return true;
	}

	@Override
	public void sendPackage(IPacket p, IIOHandler target) {
		//noinspection StatementWithEmptyBody
		if(target == SERVER) {
			//TODO
		}
		if(target == ALL) {
			for(IIOHandler ica : actors) {
				ica.sendPacket(p);
			}
		} else {
			if(target instanceof BasicIOHandler) {
				target.sendPacket(p);
			}
		}
	}

	@Override
	public void shutdown() {
		conLis.end();
		super.shutdown();
		save();
		System.exit(1);
	}

	// Used by the Server Gui to stop Server on Window closing
	public ConnectionListener getConLis() {

		return conLis;
	}

	@Override
	public String[] getActiveUserList() {
		synchronized(actors) {
			int s = actors.size();
			if(s > 0 && s < maxOnlineUser) {
				serverStatus = ServerStatus.READY;
			}
			String[] names = new String[s];
			for(int i = 0; i < s; i++) {
				names[i] = actors.get(i).getActorName();
			}
			return names;
		}

	}

	public class ConnectionListener extends Thread {
		private final int port;
		private int          logins           = 0;
		private boolean      continueLoop     = true;
		final   List<Socket> clientSocketList = new ArrayList<>();
		final IMessageHandler MH;

		/**
		 * new ConnectionListener using default port = 256
		 */
		public ConnectionListener(@NotNull IMessageHandler m) {
			MH = m;
			port = 256;
			assert m == null;
		}

		/**
		 * @param p the Port the ConnectionListener will use
		 */
		public ConnectionListener(int p, @NotNull IMessageHandler m) {

			MH = m;
			port = p;
		}

		/**
		 * Stop the ConnectionListener
		 */
		public void end() {

			continueLoop = false;
			interrupt();
			System.out.println("Closing for new Connections");

			for(Socket cl : clientSocketList) {
				try {
					cl.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}

			for(IIOHandler ica : actors) {

				if(ica instanceof BasicIOHandler) {
					BasicIOHandler io = (BasicIOHandler) ica;

					try {
						io.stop();
					} catch(Throwable e) {
						e.printStackTrace();
					}

				}
			}
			actors.clear();
		}

		@Override
		protected void finalize() throws Throwable {

			end();
			super.finalize();
		}

		/**
		 * the main Function called by the run Function ,it is listening for new Connections
		 */
		@SuppressWarnings("unchecked")
		public void listen() {
			try {

				SSLContext sc = SSLContext.getInstance("TLS");
				sc.init(null, null, null);

				SSLServerSocketFactory ssf = sc.getServerSocketFactory();
				SSLServerSocket socketS = (SSLServerSocket) ssf.createServerSocket(port);

				bb.chat.util.Socket.enableAnonConnection(socketS);

				if(MH != null) {
					MH.println("Awaiting connections on " + socketS.getLocalSocketAddress());
					while(continueLoop) {
						SSLSocket s = (SSLSocket) socketS.accept();
						if(!continueLoop) {
							s.close();
							break;
						}
						logins++;
						String n = String.valueOf(logins);
						BasicIOHandler c = new BasicIOHandler(s.getInputStream(), s.getOutputStream(), MH, false);
						c.setActorName(n);
						c.sendPacket(new RenamePacket("Client", n));

						clientSocketList.add(s);
						actors.add(c);
						Thread t = new Thread(c);
						t.start();

						//TODO move to after handshake/login sendPackage(new ChatPacket(n + " joined the Server", getActor().getActorName()), ALL);
						//println("[" + MH.getActor().getActorName() + "] " + n + " joined the Server");
						println("Client connected not yet logged in! Assigned #" + n);
						System.out.println("Connection established");
						updateUserCount();
					}
					serverStatus = ServerStatus.SHUTDOWN;
					sendPackage(new DisconnectPacket(),ALL);
					for(IIOHandler ica : actors) {
						try {
							ica.stop();
						} catch(Throwable e) {
							e.printStackTrace();
						}
					}
				}
				actors.clear();

			} catch(KeyManagementException | NoSuchAlgorithmException | IOException e) {
				e.printStackTrace();
			}

		}

		private void updateUserCount() {
			int i = 0;
			for(IIOHandler a : actors) {
				if(a.getNetworkState().ordinal() >= NetworkState.POST_HANDSHAKE.ordinal()) {
					i++;
				}
			}
			serverStatus = i > 0 ? i >= maxOnlineUser ? ServerStatus.FULL : ServerStatus.READY : ServerStatus.EMPTY;
		}

		@Override
		public void run() {

			if((port >= 0) && (port <= 65535)) {
				listen();
			} else {
				throw new RuntimeException("Port not in Range[0-65535]");
			}
		}

	}
}