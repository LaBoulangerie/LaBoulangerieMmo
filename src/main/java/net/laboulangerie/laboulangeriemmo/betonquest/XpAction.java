package net.laboulangerie.laboulangeriemmo.betonquest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;

public class XpAction implements OnlineAction {
    private final Argument<String> talentName;
    private final Argument<String> opAndValue;

    public XpAction(Argument<String> talentName, Argument<String> opAndValue) {
        this.talentName = talentName;
        this.opAndValue = opAndValue;
    }

    @Override
    public void execute(OnlineProfile profile) throws QuestException {
        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager()
                .getPlayer(profile.getPlayer());
        if (mmoPlayer == null) return;

        String talent = talentName.getValue(profile);
        String value = opAndValue.getValue(profile);
        char op = value.charAt(0);
        double xp;
        try {
            xp = Double.parseDouble(value.substring(1));
        } catch (Exception e) {
            throw new QuestException("Invalid xp value format. Use +123 or -456");
        }

        switch (op) {
            case '+' -> mmoPlayer.getTalent(talent).incrementXp(xp);
            case '-' -> mmoPlayer.getTalent(talent).decrementXp(xp);
            default -> throw new QuestException("Unknown operation: " + op + ", should be '+' or '-'");
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
