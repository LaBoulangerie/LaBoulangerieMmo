package net.laboulangerie.laboulangeriemmo.player.talent;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.player.MmoPlayer;

public class Talent {
    private double xp = 0;
    private String talentId;
    private String displayName;

    public Talent() {
        talentId = "default";
        displayName = "default";
    }

    public Talent(String talentId, String displayName) {
        this.talentId = talentId;
        this.displayName = displayName;
    }

    public double getXp() {
        return xp;
    }

    public void setXp(Double amount) {
        xp = amount;
    }

    public void incrementXp(double amount) {
        xp += amount;
    }

    public void decrementXp(double amount) {
        xp -= amount;
    }

    public int getLevel(double multiplier) {
        return (int) (multiplier * Math.sqrt(this.getXp()));
    }

    public int getLevelXp(double multiplier) {
        return (int) Math.pow(getLevel(multiplier) / multiplier, 2);
    }

    public double getXpToNextLevel(double multiplier) {
        return Math.pow((getLevel(multiplier) + 1) / multiplier, 2) - getLevelXp(multiplier);
    }

    public String getTalentId() {
        return talentId;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public Integer getNationTotalLevel (Nation nation) {
		int total = 0;
		int totalville = 0;
    	for (Town town :  nation.getTowns()) {
			totalville = 0;
        	for (Resident resident :  town.getResidents()) {
        		if (resident != null) {
        		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(resident.getUUID());
        		MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(offlinePlayer);		
        		totalville = totalville + mmoPlayer.getPalier();
        		}
            	}
        	total = total + totalville;
        	}
    	return total;
    }
    
    public Integer getTownTotalLevel (Town town) {
		int total = 0;
			total = 0;
        	for (Resident resident :  town.getResidents()) {
        		if (resident != null) {
        		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(resident.getUUID()); 
        		MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(offlinePlayer);		
        		total = total + mmoPlayer.getPalier();
        		}
            	}
        	return (int) total;
    }
}
