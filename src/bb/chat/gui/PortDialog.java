package bb.chat.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class PortDialog extends JDialog implements ActionListener {

	public int     port         = 256;
	public boolean input_gotten = false;

	private final JTextField InText = new JTextField("256");

	public PortDialog() {

		Box b = new Box(BoxLayout.Y_AXIS);
		JLabel lab = new JLabel("Enter a port \n or hit OK for the standard port 256!");
		JButton OK = new JButton("OK");
		b.add(lab);
		b.add(InText);
		b.add(OK);
		OK.addActionListener(this);
		add(b);
		addWindowListener(new WinLis());
		pack();

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		try {

			port = Integer.valueOf(InText.getText());

		} catch(IllegalArgumentException e) {
			System.err.println("Input was not an Integer!\nUsing standard port 256!");
		}

		input_gotten = true;
		setVisible(false);
	}

	private class WinLis extends WindowAdapter {
		@Override
		public void windowClosed(WindowEvent arg0) {

			super.windowClosed(arg0);

			input_gotten = true;
		}

		@Override
		public void windowClosing(WindowEvent e) {

			input_gotten = true;
			super.windowClosing(e);
		}
	}

}
