package Grotznak.bcVote;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class bcVote extends JavaPlugin{	
	bcvPlayerListener pListener = new bcvPlayerListener();
	private Logger log;
	//private boolean debugMessages;
	
    // default configuration
    private double reqYesVotes = 0.05, minAgree = 0.5;
	private int permaOffset = 0;
	private String guiLang = "english";

	//default config text file 
	private static final String defaultConfig = 
		"# At least 'required-yes-percentage'*peopleOnServer people must vote yes, and there must be more people that voted yes than no" + '\n' + 
		"required-yes-percentage 5" + '\n' +
	 	"minimum-agree-percentage 50" + '\n' +
	 	"guiLang english"
		;

	//language config
    public Hashtable<String,String> LANG;

	//default lang text file
	private static final String defaultLang = 		
	     "VOTING_COMMANDS_HEAD=Commands:"  + '\n' +
	     "VOTING_COMMANDS_VOTE_DESC_DAY=-- Vote for daylight"  + '\n' +
	     "VOTING_COMMANDS_VOTE_DESC_NIGHT=-- Vote for nightfall"  + '\n' +
	     "VOTING_COMMANDS_VOTE_DESC_SUN=-- Vote for sunshine"  + '\n' +
	     "VOTING_COMMANDS_VOTE_DESC_RAIN=-- Vote for rain"  + '\n' +
	     "VOTING_COMMANDS_VOTE_DESC_UNDO=-- undo (reset) all you votings to default (no)"  + '\n' +
	     "INFO_TIME=current time:"  + '\n' +
	     "INFO_TIME_CLOCK=o'clock"  + '\n' +
	     "VOTE_DAY=You've voted for day"  + '\n' +
	     "VOTE_DAY_ALREADY=It's day already. Youre vote is counted anyway"  + '\n' +
	     "VOTE_NIGHT=You've voted for night"  + '\n' +
	     "VOTE_NIGHT_ALREADY=It's night already. Youre vote is counted anyway"  + '\n' +
	     "VOTE_SUN=You've voted for sun"  + '\n' +
	     "VOTE_SUN_ALREADY=The sun is shining. Youre vote is counted anyway"  + '\n' +
	     "VOTE_RAIN=You've voted for rain"  + '\n' +
	     "VOTE_RAIN_ALREADY=It's still raining. Youre vote is counted anyway"  + '\n' +
	     "VOTE_TIME_CHANGE=Changing time..."  + '\n' +
	     "VOTE_WEATHER_CHANGE=Changing weather..."  + '\n' +
	     "SUM_HEAD=There are %yes% YES votes and %no% NO votes of %all% total now. " + '\n' +
	     "SUM_BODY=%votes% % voted at all and a fraction of %yespercentage% % Voted YES " + '\n' +
	     "SUM_FOOT=For are succesfull Vote you need %req% % yes Votes (min) and a majority of %min% % Votes" + '\n' +
	     "TRANSLATION=translated by the same guy" + '\n' +
	     "" 
		;
	
		
	@Override
	public void onDisable() {
		
	}

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		log = Logger.getLogger("Minecraft");
		printlog("BlockCraftVote loaded");
		
		loadConfigFile();
		loadLanguageFile();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_QUIT, pListener, Priority.Normal, this);
	   
		//push config values
		pListener.config(reqYesVotes, minAgree, permaOffset, LANG);
		//pListener.language();
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
				loadConfigFile();
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
				}else if (contents[0].equals("guiLang")){
						guiLang = (contents[1]);
				}
			}
		}
	}
	
	
	public void loadLanguageFile(){
		//check for configurations files or create them
		 File folder = new File("plugins" + File.separator + "bcVote");
	        if (!folder.exists()) {
	            folder.mkdir();
	        }
	        
	        File langFile = new File(folder.getAbsolutePath() + File.separator + guiLang + ".lang");
	        if (langFile.exists()){
	        	printlog("loading language file: " + guiLang);
	        	Scanner sc = null;
	        	try {
					sc = new Scanner(langFile);
					LANG = bcVoteConfig.langGenerate(sc);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
	        }else{
	        	printlog("creating language file: " + guiLang);
	        	BufferedWriter out = null;
	        	try {
					langFile.createNewFile();
					out = new BufferedWriter(new FileWriter(langFile));
					out.write(defaultLang);
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				loadLanguageFile();	
	        }
		
	}
	

	
}


