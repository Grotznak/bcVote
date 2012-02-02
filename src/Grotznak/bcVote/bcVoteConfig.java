package Grotznak.bcVote;


import java.util.Hashtable;
import java.util.Scanner;

public class bcVoteConfig {
	public static Hashtable<String,String> langGenerate(Scanner sc)
	{
		Hashtable<String,String> h = new Hashtable<String,String>();

		while(sc.hasNext()){
			String thisline = sc.nextLine();
			String[] contents = thisline.split("=");
			if (contents.length > 1){
				h.put(contents[0],contents[1]);
			}
		}	     
		return h;
	}
}