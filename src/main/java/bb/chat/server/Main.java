package bb.chat.server;

import bb.chat.enums.Bundles;
import bb.chat.gui.ChatServerGUI;
import bb.chat.gui.PortDialog;
import bb.chat.interfaces.IBasicChatPanel;

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
	private static final Logger logger       = ServerConstants.getLogger(Main.class);
	private static int          port         = 256;
	private static boolean      portProvided = false;
	private static boolean      gui          = true;

	/**
	 * @param tArgs just the usual tArgs to startup a java Program
	 */
	public static void main(String[] tArgs) {

		Logger.getLogger("").setLevel(Level.ALL);
		logger.fine(Bundles.LOG_TEXT.getString("log.start.starting"));

		//loop thought the Arguments
		for(String s : tArgs) {
			//noinspection HardCodedStringLiteral
			if(s.equals("nogui")) {
				gui = false;
			}
			//noinspection HardCodedStringLiteral
			if(s.startsWith("port=")) {
				//noinspection HardCodedStringLiteral
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
		logger.info("Starting Server in GUI mode!");
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
				logger.info("Aborting Server Startup!"+System.lineSeparator()+"Getting Port failed!");
				System.exit(0);
			}
		}

		new ChatServerGUI(port);
	}

	public static void noGuiMode() {
		logger.info("Starting Server in nogui mode!");
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
			logger.fine("Console found, using Console");
			while(keepGoing) {
				s = System.console().readLine();
				bc.message(s);
			}
		} else {
			logger.fine("No Console found,using Scanner!");
			Scanner in = new Scanner(System.in);
			while(keepGoing) {
				s = in.nextLine();
				bc.message(s);
			}
		}
		System.exit(0);
	}
}