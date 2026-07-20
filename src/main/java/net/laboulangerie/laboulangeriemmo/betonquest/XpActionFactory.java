package net.laboulangerie.laboulangeriemmo.betonquest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

public class XpActionFactory implements PlayerActionFactory {
    @Override
    public PlayerAction parsePlayer(Instruction instruction) throws QuestException {
        Argument<String> talentName = instruction.string().get();
        Argument<String> opAndValue = instruction.string().get();
        return new OnlineActionAdapter(new XpAction(talentName, opAndValue));
    }
}
