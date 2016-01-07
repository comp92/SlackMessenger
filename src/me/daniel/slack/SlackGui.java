package me.daniel.slack;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;

public class SlackGui extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final int FRAME_WIDTH=400, START_X = 55, WIDTH = FRAME_WIDTH - 10 - START_X;
	
	private SlackApi api;

	public SlackGui(String[] params) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(ClassNotFoundException e) {} catch(InstantiationException e) {} catch(IllegalAccessException e) {} catch(UnsupportedLookAndFeelException e) {}
		setTitle("Slack Messenger v1.0 - By Comp");
		setResizable(false);
		setSize(FRAME_WIDTH,410);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setLocationRelativeTo(null);
		//API URL
		final JComboBox<String> apiBox = new JComboBox<String>(params[0].split(","));
//		System.out.println("Params 0: \"" + params[0] + "\"");
		apiBox.setBounds(START_X,10,WIDTH,20);
		add(newLabel("API: ", 10,5,50,30));
		add(apiBox);
		
		//Channel
		final JTextField channel = new JTextField(params[2]);
		channel.setBounds(START_X,40,WIDTH,20);
		add(channel);
		add(newLabel("Channel: ", 10,35,50,30));
		
		//Nick
		final JTextField nick = new JTextField(params[1]);
		nick.setBounds(START_X,70,WIDTH,20);
		add(nick);
		add(newLabel("Nick: ", 10,65,50,30));
		
		//Emoji
		String tmp = params[3];
		if(!tmp.startsWith(":")) tmp = ":" + tmp; //Ensures the emoji has the proper format
		if(!tmp.endsWith(":")) tmp = tmp + ":";
		final JTextField emoji = new JTextField(tmp);
		emoji.setBounds(START_X,100,WIDTH,20);
		add(emoji);
		add(newLabel("Icon: ", 10,95,50,30));
		
		//Msg
		final JTextArea msg = new JTextArea();
		msg.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
		msg.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		msg.setWrapStyleWord(true);
		msg.setLineWrap(true);
		final JScrollPane sp = new JScrollPane(msg, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBounds(10,135,getWidth()-20,200);
		add(sp);
		
		//Save config
		final JButton saveBtn = new JButton("Save");
		saveBtn.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
		saveBtn.setBounds(getWidth()-135,345,60,30);
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] params = new String[apiBox.getItemCount()];
				for(int i = 0; i<apiBox.getItemCount(); i++) {
					params[i] = apiBox.getItemAt(i);
				}
				if(Main.saveConfig(params, nick.getText().trim(), channel.getText().trim(), emoji.getText().trim())) {
					alert("Success", "The config has been successfully saved."
							+ "\n\nThe config is located at \"" + Main.config.getAbsolutePath() + "\""
					, JOptionPane.INFORMATION_MESSAGE);
				} else {
					alert("Error", "The config could not be saved!", JOptionPane.ERROR_MESSAGE);
				}
			}
			
		});
		add(saveBtn);
		
		final JButton addBtn = new JButton("Add URL");
		addBtn.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
		addBtn.setBounds(10, 345, 80, 30);
		addBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String url = JOptionPane.showInputDialog(null, "Enter the URL: ", "Add a URL", JOptionPane.QUESTION_MESSAGE);
				if (url == null || url.length() < 1) {
					return;
				}
				apiBox.addItem(url);
				apiBox.setSelectedIndex(apiBox.getItemCount()-1);
			}
		});
		add(addBtn);
		
		//Send
		final JButton sendBtn = new JButton("Send");
		sendBtn.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
		sendBtn.setBounds(getWidth()-70,345,60,30);
		sendBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((String) apiBox.getSelectedItem()).trim().equals("")) {
					alert("Error", "The API field must have a URL.", JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					api= new SlackApi(((String) apiBox.getSelectedItem()).trim());
				}
				if(channel.getText().trim().equals("")) {
					alert("Error", "The channel field must have a channel name\nChannel names start with a '#', but the # may be omitted.", JOptionPane.ERROR_MESSAGE);
					return;
				}
				//Formats the channel input. If it starts with a '#' then it is left as is. Else a '#' is added to the front.
				String finChannel = channel.getText().trim().startsWith("#") ? channel.getText().trim() : "#" + channel.getText().trim();
				if(nick.getText().trim().equals("")) {
					nick.setText("CompBot" + (int)(Math.random()*10000));
				}
				if(emoji.getText().trim().equals("")) {
					emoji.setText(":skull_and_crossbones:");
				}
				if(msg.getText().trim().equals("")) {
					alert("Error", "The message text area must have a message.", JOptionPane.ERROR_MESSAGE);
					return;
				}
				SlackMessage message = new SlackMessage();
				message.setIcon(emoji.getText().trim());
				message.setChannel(finChannel);
				message.setUsername(nick.getText().trim());
				message.setText(msg.getText().trim());
				api.call(message);
				msg.setText("");
			}
			
		});
		add(sendBtn);
		setVisible(true);
		if(((String) apiBox.getSelectedItem()).equals("")) {
			apiBox.requestFocus();
		} else if(channel.getText().trim().equals("")) {
			channel.requestFocus();
		} else if(nick.getText().trim().equals("")) {
			nick.requestFocus();
		} else if(emoji.getText().trim().equals("")) {
			emoji.requestFocus();
		} else {
			msg.requestFocus();
		}
	}
	
	private JLabel newLabel(String text, int x, int y, int width, int height) {
		JLabel label = new JLabel(text);
		label.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
		label.setBounds(x,y,width,height);
		return label;
	}
	
	private void alert(String title, String msg, int status) {
		JOptionPane.showMessageDialog(null, msg, title, status);
	}
}