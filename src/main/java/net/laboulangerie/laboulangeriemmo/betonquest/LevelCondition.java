package net.laboulangerie.laboulangeriemmo.betonquest;

import java.util.UUID;

import org.bukkit.Bukkit;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

public class LevelCondition extends Condition {

    @SuppressWarnings("deprecation")
    public LevelCondition(Instruction instruction) {
        super(instruction);
    }

    @Override
    protected Boolean execute(String playerID) throws QuestRuntimeException {
        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager()
            .getPlayer(Bukkit.getPlayer(UUID.fromString(playerID)));
        if (mmoPlayer == null) return false; //Shouldn't happen but just in case

        String talentName = "";
        try {
            talentName = instruction.getPart(1);
        } catch (InstructionParseException e) {
            e.printStackTrace();
        }
        return instruction.getAllNumbers().get(0) <= mmoPlayer.getTalent(talentName).getLevel(LaBoulangerieMmo.XP_MULTIPLIER);
    }
    
}
