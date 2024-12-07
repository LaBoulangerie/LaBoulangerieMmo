package net.laboulangerie.laboulangeriemmo.betonquest;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;

public class LevelCondition implements PlayerCondition {
    private String talentName;
    private int level;
    public LevelCondition(String talentName, int level) {
        this.talentName = talentName;
        this.level = level;
    }
    
    @Override
    public boolean check(Profile profile) throws QuestRuntimeException {
        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager()
                .getPlayer(profile.getPlayer().getPlayer());
        if (mmoPlayer == null) return false; // Shouldn't happen but just in case
    
        return level <= mmoPlayer.getTalent(talentName).getLevel();
    }

}
