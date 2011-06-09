package Grotznak.bcVote;

import java.text.DecimalFormat;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class bcvPlayerListener extends PlayerListener{
	private bcVote plugin;
	
	public Votings dayvote = new Votings("day");
	public Votings nightvote = new Votings("night");
	public Votings sunvote = new Votings("sun");
	public Votings rainvote = new Votings("rain");
	
	private double reqYesVotes, minAgree;
	private int permaOffset; 
	private Hashtable<String, String> LANG;

	public void config(double reqYesVotes, double minAgree, int permaOffset,Hashtable<String, String> LANG){
	    	this.reqYesVotes = reqYesVotes;
	    	this.minAgree = minAgree;
	    	this.permaOffset = permaOffset;
	        this.LANG = LANG;
	    }
		
	private World currentWorld = null;
	
	public boolean onPlayerCommand(CommandSender sender, Command command, String label, String[] args){
		 Object[] myconfig = {
				 reqYesVotes, minAgree
				};	
		
		Player player = (Player) sender;
		if (sender instanceof Player) {
			player = (Player) sender;

			sender.sendMessage("BlockCraft Voting:");
			currentWorld = player.getWorld();
		} else {
			plugin.printlog("onPlayerCommand - sender is not a player, skipping commands.");
			return false;
		}
		//sender.sendMessage("Event done");
		
		double nicetime =roundTwoDecimals ((player.getWorld().getTime()%24000)/1000);
		 
		String[] split = args;
		if (!label.equalsIgnoreCase("vote")) return false;

		if (split.length == 0 || (split.length == 1 && split[0].equalsIgnoreCase("help"))){
			sender.sendMessage(ChatColor.AQUA + LANG.get("VOTING_COMMANDS_HEAD"));			
			sender.sendMessage(ChatColor.AQUA + "/vote day " +LANG.get("VOTING_COMMANDS_VOTE_DESC_DAY"));
			sender.sendMessage(ChatColor.AQUA + "/vote night " +LANG.get("VOTING_COMMANDS_VOTE_DESC_NIGHT"));
			sender.sendMessage(ChatColor.AQUA + "/vote sun " +LANG.get("VOTING_COMMANDS_VOTE_DESC_SUN"));
			sender.sendMessage(ChatColor.AQUA + "/vote rain " +LANG.get("VOTING_COMMANDS_VOTE_DESC_RAIN"));
			sender.sendMessage(ChatColor.AQUA + "/vote undo " +LANG.get("VOTING_COMMANDS_VOTE_DESC_UNDO"));
			return true;
		}
		
		if(split[0].equalsIgnoreCase("info")){
			sender.sendMessage(ChatColor.AQUA + "BlockCraft-Voting created by Grotznak");
			sender.sendMessage(ChatColor.AQUA + LANG.get("INFO_TIME") + " " + nicetime + " ("+player.getWorld().getName()+")");
			sender.sendMessage(ChatColor.AQUA + "visit us at www.blockcraft.de");
			return true;
		}
		
		if (split[0].equalsIgnoreCase("day")){		
			long now = currentWorld.getTime();			
			sender.sendMessage(ChatColor.AQUA + LANG.get("VOTE_DAY"));
			now =  (now % 24000); // one day lasts 24000
			if (!isDay(now,permaOffset)){				
				if (dayvote.dovote(currentWorld,player,true,myconfig,LANG)){
				 currentWorld.setTime(permaOffset);
				 sender.sendMessage(ChatColor.AQUA + LANG.get("VOTE_TIME_CHANGE"));
				 nightvote.dovote(currentWorld,player,false,myconfig,LANG);
				}
			} else {
				 sender.sendMessage(ChatColor.AQUA + LANG.get("VOTE_DAY_ALREADY") + " " + LANG.get("INFO_TIME") + " "  + nicetime + " " +  LANG.get("INFO_TIME_CLOCK") + " ("+player.getWorld().getName()+")");			
				 nightvote.dovote(currentWorld,player,false,myconfig,LANG);
			}
		}
		
		if (split[0].equalsIgnoreCase("night")){		
			long now = currentWorld.getTime();			
			sender.sendMessage(ChatColor.AQUA + LANG.get("VOTE_NIGHT"));
			now =  (now % 24000); // one day lasts 24000
			if (isDay(now,permaOffset)){				
				if (nightvote.dovote(currentWorld,player,true,myconfig,LANG)){
				 currentWorld.setTime(permaOffset+12000);
				 sender.sendMessage(ChatColor.AQUA + LANG.get("VOTE_TIME_CHANGE"));
				 dayvote.dovote(currentWorld,player,false,myconfig,LANG);
				}
			} else {	
				 sender.sendMessage(ChatColor.AQUA + LANG.get("VOTE_NIGHT_ALREADY") + " " + LANG.get("INFO_TIME") + " "  + nicetime + " " +  LANG.get("INFO_TIME_CLOCK") + " ("+player.getWorld().getName()+")");			
				 dayvote.dovote(currentWorld,player,false,myconfig,LANG);
			}
		}
		
		if (split[0].equalsIgnoreCase("sun")){				
			sender.sendMessage(ChatColor.AQUA + LANG.get("VOTE_SUN") );
			if (!isSun(currentWorld)){				
				if (sunvote.dovote(currentWorld,player,true,myconfig,LANG)){
				 currentWorld.setWeatherDuration(1);
				 sender.sendMessage(ChatColor.AQUA + LANG.get("VOTE_WEATHER_CHANGE"));
				 rainvote.dovote(currentWorld,player,false,myconfig,LANG);
				}
			} else {				 
				 sender.sendMessage(ChatColor.AQUA + LANG.get("VOTE_SUN_ALREADY") + " " + LANG.get("INFO_TIME") + " "  + nicetime + " " +  LANG.get("INFO_TIME_CLOCK") + " ("+player.getWorld().getName()+")");			
				 rainvote.dovote(currentWorld,player,false,myconfig,LANG);
			}
		}
		if (split[0].equalsIgnoreCase("rain")){				
			sender.sendMessage(ChatColor.AQUA + LANG.get("VOTE_RAIN") );
			if (isSun(currentWorld)){				
				if (rainvote.dovote(currentWorld,player,true,myconfig,LANG)){
				 currentWorld.setStorm(true);
				 currentWorld.setWeatherDuration(4000);
				 sender.sendMessage(ChatColor.AQUA + LANG.get("VOTE_WEATHER_CHANGE"));
				 sunvote.dovote(currentWorld,player,false,myconfig,LANG);
				}
			} else {				 
				 sender.sendMessage(ChatColor.AQUA + LANG.get("VOTE_RAIN_ALREADY") + " " + LANG.get("INFO_TIME") + " "  + nicetime + " " +  LANG.get("INFO_TIME_CLOCK") + " ("+player.getWorld().getName()+")");			
				 sunvote.dovote(currentWorld,player,false,myconfig,LANG);
			}
		}
		if (split[0].equalsIgnoreCase("undo")){	
			unregisterPlayerVotes(player);
		}
		return true;
	}
	
	//delete leaving Users from permavotes
	public void onPlayerQuit(PlayerQuitEvent event){
		//Player p = event.getPlayer();
		//maybe not needet due resync on vote
		//unregisterPlayerVotes(p);
		//p.getServer().broadcastMessage(ChatColor.AQUA + "Player " + p.getDisplayName() + " deleted from all persistent votes (bcVote)");
	}
	
	public void unregisterPlayerVotes(Player p){
		Object[] myconfig = {
				 reqYesVotes, minAgree
				};
		dayvote.dovote(p.getWorld(),p,false,myconfig,LANG);	
		nightvote.dovote(p.getWorld(),p,false,myconfig,LANG);
		sunvote.dovote(p.getWorld(),p,false,myconfig,LANG);
		rainvote.dovote(p.getWorld(),p,false,myconfig,LANG);
	}
	
	private boolean isDay(long currenttime, int offset){
		return (currenttime < (12000 + offset)) && (currenttime > offset );
	}
	
	private boolean isSun(World world){
		if (world.hasStorm() || world.isThundering()){
			return false;
		} else {
			return true;
		}
		
	}
	
	double roundTwoDecimals(double d) {
    	DecimalFormat twoDForm = new DecimalFormat("#.##");
	    return Double.valueOf(twoDForm.format(d));
    }
}
