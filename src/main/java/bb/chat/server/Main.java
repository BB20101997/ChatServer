package bb.chat.server;

import bb.chat.gui.ChatServerGUI;
import bb.chat.gui.PortDialog;
import bb.chat.interfaces.IBasicChatPanel;
import bb.util.file.log.BBLogHandler;
import bb.util.file.log.Constants;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author BB20101997
 */
@SuppressWarnings("ClassNamingConvention")
class Main {
	static volatile boolean keepGoing = true;

	@SuppressWarnings("ConstantNamingConvention")
	private static final Logger log;
	private static int     port         = 256;
	private static boolean portProvided = false;
	private static boolean gui          = true;

	static {
		log = Logger.getLogger(Main.class.getName());
		//noinspection DuplicateStringLiteralInspection
		log.addHandler(new BBLogHandler(Constants.getLogFile("ChatServer")));
	}

	/**
	 * @param tArgs just the usual tArgs to startup a java Program
	 */
	public static void main(String[] tArgs) {

		Logger.getLogger("").setLevel(Level.ALL);
		log.fine("Starting...");

		//loop thought the Arguments
		for(String s : tArgs) {
			if(s.equals("nogui")) {
				gui = false;
			}
			if(s.startsWith("port=")) {
				port = Integer.valueOf(s.replace("port=", ""));
				portProvided = true;
			}
		}

		if(port > 65535) {
			throw new IllegalArgumentException("The port has to be smaller than 65535 it was " + port + ".");
		}

		if(gui) {
			guiMode();
		} else {
			noGuiMode();
		}
	}

	public static void guiMode() {
		if(!portProvided) {
			PortDialog p = new PortDialog();
			p.setVisible(true);
			while(p.isVisible()) {
				try {
					Thread.sleep(5);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(p.input_gotten) {
				port = p.port;
			} else {
				System.exit(0);
			}
		}

		new ChatServerGUI(port);
	}

	public static void noGuiMode() {
		if(!portProvided) {
			if(System.console() == null) {
				do {
					//noinspection UseOfSystemOutOrSystemErr
					System.out.println("Please provide a Port(Range:1-65535)");
					port = new Scanner(System.in).nextInt();
				} while(port > 65535);
			} else {
				do {
					//noinspection UseOfSystemOutOrSystemErr
					System.out.println("Please provide a Port(Range:1-65535)");
					port = Integer.valueOf(System.console().readLine());
				} while(port > 65535);
			}
		}

		ServerChat bc = new ServerChat(port);

		//noinspection PublicMethodWithoutLogging,LocalVariableNamingConvention
		final IBasicChatPanel BCP = new IBasicChatPanel() {
			@SuppressWarnings("UseOfSystemOutOrSystemErr")
			@Override
			public void wipeLog() {
				System.out.flush();
			}

			@SuppressWarnings("UseOfSystemOutOrSystemErr")
			@Override
			public void print(String s) {
				System.out.print(s);
			}

			@Override
			public void stop() {
				keepGoing = false;
			}
		};
		bc.setBasicChatPanel(BCP);

		String s;
		if(System.console() != null) {
			log.fine("Console found, using Console");
			while(keepGoing) {
				s = System.console().readLine();
				bc.message(s);
			}
		} else {
			log.fine("No Console found,using Scanner!");
			Scanner in = new Scanner(System.in);
			while(keepGoing) {
				s = in.nextLine();
				bc.message(s);
			}
		}
		System.exit(0);
	}
}