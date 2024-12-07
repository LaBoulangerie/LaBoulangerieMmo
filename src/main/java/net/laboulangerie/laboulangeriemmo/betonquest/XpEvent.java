package net.laboulangerie.laboulangeriemmo.betonquest;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;

public class XpEvent implements Event {
    String talentName;
    char op;
    double xp;

    public XpEvent(String talentName, char op, double xp) {
        this.talentName = talentName;
        this.op = op;
        this.xp = xp;
    }

    @Override
    public void execute(Profile profile) throws QuestRuntimeException {
        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager()
                .getPlayer(profile.getPlayer().getPlayer());
        if (mmoPlayer == null) return; // Shouldn't happen but just in case

        switch (op) {
            case '+':
                mmoPlayer.getTalent(talentName).incrementXp(xp);
                break;
            case '-':
                mmoPlayer.getTalent(talentName).decrementXp(xp);
                break;
            default:
                throw new QuestRuntimeException("Unknown operation: " + op
                        + ", should be either '+' or '-'");
        }
    }

}
