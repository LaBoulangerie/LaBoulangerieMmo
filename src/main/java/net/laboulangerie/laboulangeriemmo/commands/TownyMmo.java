package net.laboulangerie.laboulangeriemmo.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.player.talent.Talent;

public class TownyMmo implements CommandExecutor, TabCompleter{

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<String>();
            list.add("nation");
            list.add("town");
            return list;
        }
        if (args.length == 2) {
            List<String> list = new ArrayList<String>();
            list.add("leaderboard");
            list.add("see");
            return list;
        }
        if (args.length == 3) {
            List<String> list = new ArrayList<String>();
            list.add("total ");
            list.add("farmer");
            list.add("mining");
            list.add("woodcutting");
            list.add("thehunter");
            return list;
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("town") && !(args[1].equalsIgnoreCase("leaderboard"))) {
            List<String> list = new ArrayList<String>();
        	for (Town town :  TownyUniverse.getInstance().getTowns()) {
        		list.add(town.getName());
        	}
            return list;   
        }
        else if (args.length == 4 && args[0].equalsIgnoreCase("nation") && !(args[1].equalsIgnoreCase("leaderboard"))) {
            List<String> list = new ArrayList<String>();
        	for (Nation nation :  TownyUniverse.getInstance().getNations()) {
        		list.add(nation.getName());
        	}
            return list;   
        }
		return null;
	}
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args[0].equalsIgnoreCase("town")) {
			if (args[1].equalsIgnoreCase("leaderboard")) {
				if (args[2].equalsIgnoreCase("total")) {
			int total = 0;
			int villeUn = 0;
			String villeUnName = null;
			String villeDeuxName = null;
			int villeDeux = 0;
					
        	for (Town town :  TownyUniverse.getInstance().getTowns()) {
    			total = 0;
	    		total = MmoPlayer.getTownTotalLevel(town);
                	if (total > villeUn) {
                		if (villeUn != 0) {
                			if (villeUn > villeDeux) {
                				villeDeux = villeUn;  
                				}  
                			}
                		villeUn = total;
                		villeUnName = town.getName();
                	}
                	else if (total < villeUn && total > villeDeux) {
                		villeDeux = total;
                		villeDeuxName = town.getName();
                		}
                	
            	}
        	sender.sendMessage("La ville avec le plus gros total de pallier est : " + villeUnName + " avec ce nombre de palliers : " + villeUn);
        	sender.sendMessage("La ville avec le deuxième plus gros total de pallier est : " + villeDeuxName + " avec ce nombre de palliers : " + villeDeux);
        	}
				if (args[2].equalsIgnoreCase("mining") || args[2].equalsIgnoreCase("farmer") || args[2].equalsIgnoreCase("woodcutting") || args[2].equalsIgnoreCase("thehunter")) {
					int total = 0;
					int villeUn = 0;
					String villeUnName = null;
					String villeDeuxName = null;
					int villeDeux = 0;
							
		        	for (Town town :  TownyUniverse.getInstance().getTowns()) {
		    			total = 0;
			    		total = MmoPlayer.getTownTalentLevel(town, args[2]);
		                	if (total > villeUn) {
		                		if (villeUn != 0) {
		                			if (villeUn > villeDeux) {
		                				villeDeux = villeUn;  
		                				villeDeuxName = villeUnName;
		                				}  
		                			}
		                		villeUn = total;
		                		villeUnName = town.getName();
		                	}
		                	else if (total < villeUn && total > villeDeux) {
		                		villeDeux = total;
		                		villeDeuxName = town.getName();
		                		}
		                	
		            	}
		        	sender.sendMessage("La ville avec le plus gros total de niveaux de " + args[2] +" est : " + villeUnName + " avec ce nombre de palliers : " + villeUn);
		        	sender.sendMessage("La ville avec le deuxième plus gros total de pallier est : " + villeDeuxName + " avec ce nombre de palliers : " + villeDeux);
				}
				}
			if (args[1].equalsIgnoreCase("see")) {
				if (args[2].equalsIgnoreCase("total")) {
					int total = 0;
					Town town = TownyUniverse.getInstance().getTown(args[3]);
					String villeUnName = town.getName();
		    		total = MmoPlayer.getTownTotalLevel(town);	
		        	sender.sendMessage("La ville de " + villeUnName + " a un pallier total de : " + total);
				}
				if (args[2].equalsIgnoreCase("mining") || args[2].equalsIgnoreCase("farmer") || args[2].equalsIgnoreCase("woodcutting") || args[2].equalsIgnoreCase("thehunter")) {
					int total = 0;
					Town town = TownyUniverse.getInstance().getTown(args[3]);
					String villeUnName = town.getName();
		    		total = MmoPlayer.getTownTalentLevel(town, args[2]);
		        	sender.sendMessage("Le niveau total de " + villeUnName + "dans le métier " + args[2] + " est de : " + total);
				} 
			}
			}
		if (args[0].equalsIgnoreCase("nation")) {
			if (args[1].equalsIgnoreCase("leaderboard")) {
				if (args[2].equalsIgnoreCase("total")) {
			int total = 0;
			int nationUn = 0;
			String nationUnName = null;
			String nationDeuxName = null;
			int nationDeux = 0;
				
			for (Nation nation : TownyUniverse.getInstance().getNations()) {
    			total = MmoPlayer.getNationTotalLevel(nation);
        	if (total > nationUn) {
        		if (nationUn != 0) {
        			if (nationUn > nationDeux) {
        				nationDeux = nationUn;
        				nationDeuxName = nationUnName;
        				}  
        			}
        		nationUn = total;
        		nationUnName = nation.getName();
        	}
        	else if (total < nationUn && total > nationDeux) {
        		nationDeux = total;
        		nationDeuxName = nation.getName();
        		}
			}
        	sender.sendMessage("La nation avec le plus gros total de pallier est : " + nationUnName + " avec ce nombre de palliers : " + nationUn);
        	sender.sendMessage("La nation avec le deuxième plus gros total de pallier est : " + nationDeuxName + " avec ce nombre de palliers : " + nationDeux);
        	}
				if (args[2].equalsIgnoreCase("mining") || args[2].equalsIgnoreCase("farmer") || args[2].equalsIgnoreCase("woodcutting") || args[2].equalsIgnoreCase("thehunter")) {
					int total = 0;
					int nationUn = 0;
					String nationUnName = null;
					String nationDeuxName = null;
					int nationDeux = 0;
						
					for (Nation nation : TownyUniverse.getInstance().getNations()) {
		    			total = MmoPlayer.getNationTalentLevel(nation, args[2]);
		        	if (total > nationUn) {
		        		if (nationUn != 0) {
		        			if (nationUn > nationDeux) {
		        				nationDeux = nationUn;  
		        				}  
		        			}
		        		nationUn = total;
		        		nationUnName = nation.getName();
		        	}
		        	else if (total < nationUn && total > nationDeux) {
		        		nationDeux = total;
		        		nationDeuxName = nation.getName();
		        		}
					}
		        	sender.sendMessage("La nation avec le plus gros total de niveaux de " + args[2] +" est : " + nationUnName + " avec ce nombre de palliers : " + nationUn);
		        	sender.sendMessage("La deuxième nation avec le plus gros total de niveaux de " + args[2] +" est : " + nationDeuxName + " avec ce nombre de palliers : " + nationDeux);
				}
				}
			if (args[1].equalsIgnoreCase("see")) {
				if (args[2].equalsIgnoreCase("total")) {
					int total = 0;
					Nation nation = TownyUniverse.getInstance().getNation(args[3]);
					String nationUnName = nation.getName();
					total = MmoPlayer.getNationTotalLevel(nation);
		        	sender.sendMessage("La ville de " + nationUnName + " a un pallier total de : " + total);
				}
				if (args[2].equalsIgnoreCase("mining") || args[2].equalsIgnoreCase("farmer") || args[2].equalsIgnoreCase("woodcutting") || args[2].equalsIgnoreCase("thehunter")) {
					int total = 0;
					Nation nation = TownyUniverse.getInstance().getNation(args[3]);
					String nationUnName = nation.getName();
					total = MmoPlayer.getNationTalentLevel(nation, args[2]);
		        	sender.sendMessage("Le niveau total de " + nationUnName + "dans le métier " + args[2] + " est de : " + total);
				} 
			}
			}
		return false;	
	}
}
