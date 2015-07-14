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

		Box box = new Box(BoxLayout.Y_AXIS);
		JLabel lab = new JLabel("Enter a port "+ System.lineSeparator()+" or hit OK for the standard port 256!");
		JButton OK = new JButton("OK");
		box.add(lab);
		box.add(InText);
		box.add(OK);
		OK.addActionListener(this);
		add(box);
		addWindowListener(new WinLis());
		pack();

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		try {
			port = Integer.valueOf(InText.getText());
			input_gotten = true;
		} catch(IllegalArgumentException e) {
			//TODO: Log
			input_gotten = false;
		}
		setVisible(false);
	}

	private class WinLis extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {

			super.windowClosing(e);
		}

		@Override
		public void windowClosed(WindowEvent arg0) {

			super.windowClosed(arg0);

		}
	}

}
