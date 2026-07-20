package net.laboulangerie.laboulangeriemmo.betonquest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

public class LevelConditionFactory implements PlayerConditionFactory {
    @Override
    public PlayerCondition parsePlayer(Instruction instruction) throws QuestException {
        Argument<String> talentName = instruction.string().get();
        Argument<Number> level = instruction.number().get();
        return new OnlineConditionAdapter(new LevelCondition(talentName, level));
    }
}
