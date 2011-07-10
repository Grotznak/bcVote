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
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;

public class bcVote extends JavaPlugin{	
	bcvPlayerListener pListener = new bcvPlayerListener();
	private Logger log;
    public static PermissionHandler permissionHandler;
	//private boolean debugMessages;
	
  
	// config
    public Hashtable<String,String> CONFIG;
	//default config text file 
	private static final String defaultConfig = 
		"required-yes-percentage=33" + '\n' +
	 	"minimum-agree-percentage=51" + '\n' +
	 	"broadcast-votes=true" + '\n' +
	 	"guiLang=english" + '\n' +
	 	"debug=false" + '\n' +
	 	""
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
	     "VOTE_NO_PERMISSION=You have no permission to vote for that" + '\n' +
	     "VOTE_BROADCAST_PERMISSION=Someone has started a vote for %vote%. Currently there are %yes% YES-votes and %no% NO-votes." + '\n' +
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
		
		loadConfig();		
		loadLanguageFile();		
		pListener.config(CONFIG,LANG);
		
		setupPermissions();
		 
		//PluginManager pm = getServer().getPluginManager();
		//pm.registerEvent(Event.Type.PLAYER_QUIT, pListener, Priority.Normal, this);
	   
		
		
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
	

	
	private void loadConfig(){ 
		//check for configurations files or create them
		 File folder = new File("plugins" + File.separator + "bcVote");
	        if (!folder.exists()) {
	            folder.mkdir();
	        }
	        
	        File ConfigFile = new File(folder.getAbsolutePath() + File.separator + "config.yml");
	        if (ConfigFile.exists()){
	        	printlog("loading config file: done" );
	        	Scanner sc = null;
	        	try {
					sc = new Scanner(ConfigFile);
					CONFIG = bcVoteConfig.langGenerate(sc);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
	        }else{
	        	printlog("creating config file: done");
	        	BufferedWriter out = null;
	        	try {
					ConfigFile.createNewFile();
					out = new BufferedWriter(new FileWriter(ConfigFile));
					out.write(defaultConfig);
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				loadConfig();	
	        }
	}
	
	
	private void loadLanguageFile(){
		//check for configurations files or create them
		 File folder = new File("plugins" + File.separator + "bcVote");
	        if (!folder.exists()) {
	            folder.mkdir();
	        }
	        
	        File langFile = new File(folder.getAbsolutePath() + File.separator + CONFIG.get("guiLang") + ".lang");
	        if (langFile.exists()){
	        	printlog("loading language file: " + CONFIG.get("guiLang"));
	        	Scanner sc = null;
	        	try {
					sc = new Scanner(langFile);
					LANG = bcVoteConfig.langGenerate(sc);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
	        }else{
	        	printlog("creating language file: " + CONFIG.get("guiLang"));
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
	
	private void setupPermissions() {
	      Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

	      if (this.permissionHandler == null) {
	          if (permissionsPlugin != null) {
	              this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	              printlog("found Permisions plugin");
	          } else {
	              log.info("Permission system not detected, defaulting to OP");
	          }
	      }
	  }
	
}


