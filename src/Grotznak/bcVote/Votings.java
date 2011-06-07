package Grotznak.bcVote;

import java.util.List;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class Votings {
	public String name;
	public List<Player> all;
	public List<Player> yes;
	public List<Player> no;
	
	public Votings(String name) {
		this.name = name;
		this.all = null;
		this.yes = null;
		this.no =null; 		 
	}

    public boolean dovote(World world, Player voter, boolean vote, Object[] myconfig) {
	   Server s = voter.getServer();
	   this.all = s.matchPlayer("");
	   
	   if (null==this.yes) {
		   		   this.yes = s.matchPlayer("");
		   this.yes.clear();	   		   
	   }
	   
	   if (null==this.no) {
   		   this.no = s.matchPlayer("");
		   //this.no.clear();	   		   
	   }

	   Double req = (Double) myconfig[0];
	   Double min = (Double) myconfig[1];
	   Double allcount = (double) this.all.size();
	   
	   if (vote){		  
			  if(!this.yes.contains(voter)){
				this.yes.add(voter);
				//  voter.sendMessage("is put in");
			  }
			  if(this.no.contains(voter)){
				  this.no.remove(voter);
			  }
			  /* voter.sendMessage("starter" + voter.getDisplayName());
			  voter.sendMessage("all" + this.all);
			  voter.sendMessage("yesvotes" + this.yes);
			  voter.sendMessage("novotes" + this.no);
			  voter.sendMessage("config" + myconfig[0] + ", "+ myconfig[1] );*/
			  
			  Double yescount = (double) this.yes.size();
			  Double nocount = (double) this.no.size();
	
		      Double allvotes = yescount + nocount;
			   voter.sendMessage("Currently there are " + yescount + " YES and " +nocount + " NO of "+allcount+ " Total. ");
			   voter.sendMessage("There are "+ (yescount / allcount )*100  + " % yes Votes (min) and a majority of " + ((allvotes/yescount))*100 + " % Votes");
			   voter.sendMessage("For are succesfull Vote you need "+ req*100 + " % yes Votes (min) and a majority of " + min*100+ " % Votes");
	
			 if (((yescount / allcount ) > req ) &&  ((allvotes/yescount)>=min)){
				 //voter.sendMessage("send true");
				 return true;
				 
			 } else {
				 //voter.sendMessage("send false");
				 return false;		
			 }
	  } else {
			  if(this.yes.contains(voter)){
				  this.yes.remove(voter);
			  }
			  if(!no.contains(voter)){
				  no.add(voter);
			  }
		      return false;
	  } 
	  
	}	

}

