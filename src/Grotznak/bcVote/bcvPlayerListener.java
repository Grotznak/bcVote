package Grotznak.bcVote;

import java.text.DecimalFormat;

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
	//private int voteTime, voteFailDelay, votePassDelay, voteRemindCount;
	//private boolean perma, bedVote;
	//private static final int nightstart = 14000;
	//private Set<String> canStartVotes = null;

	public void config(double reqYesVotes, double minAgree, int permaOffset, int voteTime, int voteFailDelay, int votePassDelay, int voteRemindCount, boolean perma, boolean bedVote){
	    	this.reqYesVotes = reqYesVotes;
	    	this.minAgree = minAgree;
	    	this.permaOffset = permaOffset;
	    	//this.voteTime = voteTime;
	    	//this.voteFailDelay = voteFailDelay;
	    	//this.votePassDelay = votePassDelay;
	    	//this.voteRemindCount = voteRemindCount;
	    	//this.perma = perma;
	    	//this.bedVote = bedVote;
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
			sender.sendMessage(ChatColor.AQUA + "voting commands");			
			sender.sendMessage(ChatColor.AQUA + "/vote day -- vote for daylight");
			sender.sendMessage(ChatColor.AQUA + "/vote night -- let the night begin");
			sender.sendMessage(ChatColor.AQUA + "/vote sun -- stop the rain");
			sender.sendMessage(ChatColor.AQUA + "/vote rain -- for singing");
			sender.sendMessage(ChatColor.AQUA + "/vote undo -- unregister from all votes");
			return true;
		}
		
		if(split[0].equalsIgnoreCase("info")){
			sender.sendMessage(ChatColor.AQUA + "BlockCraft-Voting created by Grotznak");
			sender.sendMessage(ChatColor.AQUA + "Current time: " + nicetime + " ("+player.getWorld().getName()+")");
			sender.sendMessage(ChatColor.AQUA + "visit us at www.blockcraft.de");
			return true;
		}
		
		if (split[0].equalsIgnoreCase("day")){		
			long now = currentWorld.getTime();			
			sender.sendMessage(ChatColor.AQUA + "you have voted for day");
			now =  (now % 24000); // one day lasts 24000
			if (!isDay(now,permaOffset)){				
				if (dayvote.dovote(currentWorld,player,true,myconfig)){
				 currentWorld.setTime(permaOffset);
				 sender.sendMessage(ChatColor.AQUA + "Changing time");
				 nightvote.dovote(currentWorld,player,false,myconfig);
				}
			} else {
				 sender.sendMessage(ChatColor.AQUA + "It's allready day." + " Current time: " + nicetime + " ("+player.getWorld().getName()+")");			
				 nightvote.dovote(currentWorld,player,false,myconfig);
			}
		}
		
		if (split[0].equalsIgnoreCase("night")){		
			long now = currentWorld.getTime();			
			sender.sendMessage(ChatColor.AQUA + "you have voted for night");
			now =  (now % 24000); // one day lasts 24000
			if (isDay(now,permaOffset)){				
				if (nightvote.dovote(currentWorld,player,true,myconfig)){
				 currentWorld.setTime(permaOffset+12000);
				 sender.sendMessage(ChatColor.AQUA + "Changing time");
				 dayvote.dovote(currentWorld,player,false,myconfig);
				}
			} else {	
				 sender.sendMessage(ChatColor.AQUA + "It's allready night." + " Current time: " + nicetime + " o'clock ("+player.getWorld().getName()+")");			
				 dayvote.dovote(currentWorld,player,false,myconfig);
			}
		}
		
		if (split[0].equalsIgnoreCase("sun")){				
			sender.sendMessage(ChatColor.AQUA + "you have voted for sun" );
			if (!isSun(currentWorld)){				
				if (sunvote.dovote(currentWorld,player,true,myconfig)){
				 currentWorld.setWeatherDuration(1);
				 sender.sendMessage(ChatColor.AQUA + "Changing weather");
				 rainvote.dovote(currentWorld,player,false,myconfig);
				}
			} else {				 
				 sender.sendMessage(ChatColor.AQUA + "The sun is allready shining." + " Current time: " +nicetime+ " ("+player.getWorld().getName()+")");			
				 rainvote.dovote(currentWorld,player,false,myconfig);
			}
		}
		if (split[0].equalsIgnoreCase("rain")){				
			sender.sendMessage(ChatColor.AQUA + "you have voted for rain" );
			if (isSun(currentWorld)){				
				if (rainvote.dovote(currentWorld,player,true,myconfig)){
				 currentWorld.setStorm(true);
				 currentWorld.setWeatherDuration(4000);
				 sender.sendMessage(ChatColor.AQUA + "Changing weather");
				 sunvote.dovote(currentWorld,player,false,myconfig);
				}
			} else {				 
				 sender.sendMessage(ChatColor.AQUA + "It's allready raining." + " Current time: " + nicetime + " ("+player.getWorld().getName()+")");			
				 sunvote.dovote(currentWorld,player,false,myconfig);
			}
		}
		if (split[0].equalsIgnoreCase("undo")){	
			unregisterPlayerVotes(player);
		}
		return true;
	}
	
	//delete leaving Users from permavotes
	public void onPlayerQuit(PlayerQuitEvent event){
		Player p = event.getPlayer();	
		unregisterPlayerVotes(p);
		//p.getServer().broadcastMessage(ChatColor.AQUA + "Player " + p.getDisplayName() + " deleted from all persistent votes (bcVote)");
	}
	
	public void unregisterPlayerVotes(Player p){
		Object[] myconfig = {
				 reqYesVotes, minAgree
				};
		dayvote.dovote(p.getWorld(),p,false,myconfig);	
		nightvote.dovote(p.getWorld(),p,false,myconfig);
		sunvote.dovote(p.getWorld(),p,false,myconfig);
		rainvote.dovote(p.getWorld(),p,false,myconfig);
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
