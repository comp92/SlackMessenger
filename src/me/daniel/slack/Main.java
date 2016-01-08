package me.daniel.slack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class Main {
	
	private static final String[] configHelp = new String[] {
			"#This is the config file for Slack Messenger",
			"#Four settings are parsed:",
			"#url=<SLACK WEBHOOKS API URL HERE>",
			"#nick=<YOUR NICKNAME>",
			"#channel=<A CHANNEL IN YOUR SLACK TEAM>",
			"#icon=<YOUR ICON>",
			"#quitprompt=<YES|NO>",
			"#The settings must begin the line",
			"#Multiple URL declarations may be made",
			"#If quitprompt is set to no, you will not be prompted when quitting the program.",
			"#It is recommended to use the interface to save the config instead of editing on your own"
	};
	
	public static File config;
	public static void main(String... args) {
		new SlackGui(init());
	}
	
	private static String[] init() {
		String[] params = new String[5];
		for(int i=0;i<params.length;i++) {
			params[i]="";
		}
		String dir = System.getProperty("user.home") + File.separator + ".slackmsg";
		String cfg = "config.txt";
		File file = new File(dir);
		file.mkdirs();
		config = new File(dir + File.separator + cfg);
		if(!config.exists()) 
			try {
				config.createNewFile();
				PrintStream fw = new PrintStream(config);
				for(int i = 0; i < configHelp.length; i++) {
					fw.println(configHelp[i]);
				}
				fw.close();
			} catch (IOException e) {
				System.err.println("Could not create config file at: " + config.getAbsolutePath());
			}
		try {
			Scanner scanner = new Scanner(config);
			while(scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				if(line.startsWith("url=")) {
					if(line.equals("url=")) continue;
					String tmp = line.split("url=")[1].trim();
					if(tmp.equals("") || tmp.equals("null")) continue;
					params[0] = params[0] + (params[0].equals("")? "" : ",") + line.split("url=")[1].trim(); //fmt: URL,URL,URL,URL,....
					continue;
				}
				if(line.startsWith("nick=")) {
					params[1] = line.split("nick=")[1].trim();
					continue;
				}
				if(line.startsWith("channel=")) {
					params[2] = line.split("channel=")[1].trim();
					continue;
				}
				if(line.startsWith("icon=")) {
					params[3] = line.split("icon=")[1].trim();
					continue;
				}
				if(line.startsWith("quitprompt=")) {
					if(line.equals("quitprompt=")) continue;
					String tmp = line.split("quitprompt=")[1].trim();
					if(tmp.equals("")) continue;
					params[4] = line.split("quitprompt=")[1].trim();
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.err.println("Could not find config file at: " + config.getAbsolutePath());
		}
		return params;
	}
	
	public static boolean saveConfig(String[] urls, String nick, String channel, String emoji, boolean quitPrompt) {
		try {
			PrintStream fw = new PrintStream(config);
			for(int i = 0; i < configHelp.length; i++) {
				fw.println(configHelp[i]);
			}
			for(String s : urls) {
				if(s==null || s.trim().equals("") || s.trim().equals("null")) continue; //Makes sure that no empty strings are sent, eliminates empty entries in the config.
				fw.println("url=" + s) ;
			}
			fw.println("nick="+nick);
			fw.println("icon="+emoji);
			fw.println("channel="+channel);
			fw.println("quitprompt="+((quitPrompt)? "yes" : "no"));
			fw.close();
			return true;
		} catch (FileNotFoundException e) {
			System.err.println("Could not find config file at: " + config.getAbsolutePath());
		}
		return false;
	}
}