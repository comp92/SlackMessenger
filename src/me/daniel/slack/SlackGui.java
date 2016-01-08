package me.daniel.slack;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;

public class SlackGui extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final int FRAME_WIDTH=400, START_X = 55, WIDTH = FRAME_WIDTH - 10 - START_X;
	private static boolean quitPrompt = true;
	
	private SlackApi api;

	public SlackGui(String[] params) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(ClassNotFoundException e) {} catch(InstantiationException e) {} catch(IllegalAccessException e) {} catch(UnsupportedLookAndFeelException e) {}
		setTitle("Slack Messenger v1.2");
		setResizable(false);
		setSize(FRAME_WIDTH,430);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setLocationRelativeTo(null);
		
		if(params[4].equalsIgnoreCase("no")) quitPrompt=false;
		
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
		final JTextField emoji = new JTextField(params[3]);
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

		
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		
		//Remove URL menu bar entry
		final JMenuItem rmItem = new JMenuItem("Remove URL");
		rmItem.setMnemonic(KeyEvent.VK_R);
		if(apiBox.getItemCount()<1) {
			rmItem.setEnabled(false);
		}
		rmItem.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent event) {
		    	if(apiBox.getItemCount()==0) return;
				int in = JOptionPane.showConfirmDialog(null, "This will remove the current URL.\nYou cannot undo this. Are you sure?", "Remove URL", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(in==JOptionPane.YES_OPTION) {
					apiBox.removeItemAt(apiBox.getSelectedIndex());
					if(apiBox.getItemCount()<1) {
						rmItem.setEnabled(false);
					}
				}
		    }
		});
		//Add URL menu bar entry
				JMenuItem addItem = new JMenuItem("Add URL");
				addItem.setMnemonic(KeyEvent.VK_A);
				addItem.addActionListener(new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent event) {
				    	String url = JOptionPane.showInputDialog(null, "Enter the URL: ", "Add a URL", JOptionPane.QUESTION_MESSAGE);
						if (url == null || url.length() < 1) {
							return;
						}
						apiBox.addItem(url);
						apiBox.setSelectedIndex(apiBox.getItemCount()-1);
						if(!rmItem.isEnabled()) rmItem.setEnabled(true);
				    }
				});
				file.add(addItem);
		file.add(rmItem);
		
		//Save config menu bar entry
		JMenuItem saveItem = new JMenuItem("Save config");
		saveItem.setMnemonic(KeyEvent.VK_S);
		saveItem.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent event) {
		    	String[] params = new String[apiBox.getItemCount()];
				for(int i = 0; i<apiBox.getItemCount(); i++) {
					params[i] = apiBox.getItemAt(i);
				}
				if(Main.saveConfig(params, nick.getText().trim(), channel.getText().trim(), emoji.getText().trim(),quitPrompt)) {
					alert("Success", "The config has been successfully saved."
							+ "\n\nThe config is located at \"" + Main.config.getAbsolutePath() + "\""
					, JOptionPane.INFORMATION_MESSAGE);
				} else {
					alert("Error", "The config could not be saved!", JOptionPane.ERROR_MESSAGE);
				}
		    }
		});
		file.add(saveItem);
		file.addSeparator();
		
		//Exit application menu bar entry
		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.setMnemonic(KeyEvent.VK_Q);
		quitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!quitPrompt) System.exit(0);
				int in = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?\nThis prompt can be disabled from\nHelp->Toggle Quit Prompt", "Quit application?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(in==JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
			
		});
		file.add(quitItem);
		
		menubar.add(file);
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		
		JMenuItem toggleQuit = new JMenuItem("Toggle Quit Prompt");
		toggleQuit.setMnemonic(KeyEvent.VK_T);
		toggleQuit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int in = JOptionPane.showConfirmDialog(null, "Would you like to be prompted on attempting to quit?\nThis setting is currently set to: " + ((quitPrompt)? "yes" : "no") + "\nRemember to save your config after you change this.", "Toggle Quit Prompt", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				System.out.println(in);
				if(in==-1) return;
				if(in == JOptionPane.YES_OPTION) {
					quitPrompt=true;
				} else {
					quitPrompt=false;
				}
			}
			
		});
		helpMenu.add(toggleQuit);
		helpMenu.addSeparator();
		
		JMenuItem about = new JMenuItem("About");
		about.setMnemonic(KeyEvent.VK_A);
		about.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame aboutFrame = new JFrame("Slack Messenger - About");
				aboutFrame.setSize(550,220);
				aboutFrame.setLocationRelativeTo(null);
				aboutFrame.setVisible(true);
				aboutFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				aboutFrame.setResizable(false);
				JTextPane aboutTxt = new JTextPane();
		        appendToPane(aboutTxt, "Slack messenger - Send messages to teams on Slack without a browser.\n", new Color(0,0,128));
		        appendToPane(aboutTxt, "Created by ", Color.BLACK);
		        appendToPane(aboutTxt, "Comp (Daniel)", new Color(138,43,226)); 
		        appendToPane(aboutTxt, ", 7 January 2016\n", Color.BLACK);
		        appendToPane(aboutTxt, "Credits:\n", Color.BLACK);
		        appendToPane(aboutTxt, "https://github.com/gpedro/slack-webhook : The API used in this program.\n", Color.BLUE);
		        appendToPane(aboutTxt, "\nThis program is on github:\n", Color.BLACK);
		        appendToPane(aboutTxt, "https://github.com/comp92/SlackMessenger", Color.RED);

				aboutTxt.setEditable(false);
		        aboutFrame.add(aboutTxt);
			}
			
		});
		
		helpMenu.add(about);
		menubar.add(helpMenu);
		
		setJMenuBar(menubar);
		
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
	
	//http://stackoverflow.com/questions/9650992/how-to-change-text-color-in-the-jtextarea
	private void appendToPane(JTextPane tp, String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }
}