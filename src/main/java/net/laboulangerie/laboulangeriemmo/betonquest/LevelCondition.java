package net.laboulangerie.laboulangeriemmo.betonquest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.OnlineCondition;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;

public class LevelCondition implements OnlineCondition {
    private final Argument<String> talentName;
    private final Argument<Number> level;

    public LevelCondition(Argument<String> talentName, Argument<Number> level) {
        this.talentName = talentName;
        this.level = level;
    }

    @Override
    public boolean check(OnlineProfile profile) throws QuestException {
        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager()
                .getPlayer(profile.getPlayer());
        if (mmoPlayer == null) return false;

        String talent = talentName.getValue(profile);
        int requiredLevel = level.getValue(profile).intValue();
        return requiredLevel <= mmoPlayer.getTalent(talent).getLevel();
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
