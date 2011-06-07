package Grotznak.bcVote;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
//import org.bukkit.entity.Player;

public class bcVote extends JavaPlugin{
	
	bcvPlayerListener pListener = new bcvPlayerListener();
	private Logger log;

	
    // default configuration
    private double reqYesVotes = 0.05, minAgree = 0.5;
	private int permaOffset = 0; 
	private int voteTime = 30000, voteFailDelay = 30000, votePassDelay = 50000, voteRemindCount = 2;
	private boolean bedVote = false;
	private boolean perma = false;
	//private boolean debugMessages;
	private static final String defaultConfig = 
		"# At least 'required-yes-percentage'*peopleOnServer people must vote yes, and there must be more people that voted yes than no" + '\n' + 
		"required-yes-percentage 5" + '\n' +
	 	"minimum-agree-percentage 50" + '\n' +
		"vote-fail-delay 30" + '\n' +
		"vote-pass-delay 50" + '\n' +
		"vote-time 30" + '\n' +
		"reminders 2" + '\n' +
		"bedvote yes" + '\n' +
		"permanent no";
	
	
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		log = Logger.getLogger("Minecraft");
		printlog("BlockCraftVote loaded");
		
		loadConfigFile();

		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_QUIT, pListener, Priority.Normal, this);
	   
		//push config values
		pListener.config(reqYesVotes, minAgree, permaOffset, voteTime, voteFailDelay, votePassDelay, voteRemindCount, perma, bedVote);

	    
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command command,
    		String label, String[] args) {
    	return pListener.onPlayerCommand(sender, command, label, args);
    }
	
	public void printlog(String message) {
		PluginDescriptionFile pdfFile = getDescription();
		log.info("[" + pdfFile.getName() + " Version: " + pdfFile.getVersion() + "] " + message);
	}
	
	public void loadConfigFile(){
		//check for configurations files or create them
		 File folder = new File("plugins" + File.separator + "bcVote");
	        if (!folder.exists()) {
	            folder.mkdir();
	        }
	        
	        File configFile = new File(folder.getAbsolutePath() + File.separator +"config.yml");
	        if (configFile.exists()){
	        	printlog("loading properties file.");
	        	Scanner sc = null;
	        	try {
					sc = new Scanner(configFile);
					parseConfig(sc);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
	        }else{
	        	printlog("creating properties file.");
	        	BufferedWriter out = null;
	        	try {
					configFile.createNewFile();
					out = new BufferedWriter(new FileWriter(configFile));
					out.write(defaultConfig);
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
		
	}
	
	private void parseConfig(Scanner sc){ 
		while(sc.hasNext()){
			String thisline = sc.nextLine();
			String[] contents = thisline.split(" ");
			if (contents.length > 1){
				if (contents[0].equals("minimum-agree-percentage")){
						minAgree = Integer.parseInt(contents[1]);
						minAgree /= 100;
				}else if (contents[0].equals("required-yes-percentage")){
						reqYesVotes = Integer.parseInt(contents[1]);
						reqYesVotes /= 100;
				}else if (contents[0].equals("vote-fail-delay")){
						voteFailDelay = Integer.parseInt(contents[1]) * 1000;
				}else if (contents[0].equals("vote-pass-delay")){
						votePassDelay = Integer.parseInt(contents[1]) * 1000;
				}else if (contents[0].equals("vote-time")){
						voteTime = Integer.parseInt(contents[1]) * 1000;
				}else if (contents[0].equals("reminders")){
						voteRemindCount = Integer.parseInt(contents[1]);
				}else if (contents[0].equals("permanent")){
						perma = contents[1].equals("yes");
				}else if (contents[0].equals("bedvote")){
						bedVote = contents[1].equals("yes");
				}else if (contents[0].equals("debug-messages")){
					//debugMessages = contents[1].equals("yes");
				}
			}
		}
	}

	
	
}


