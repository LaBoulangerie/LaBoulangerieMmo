package net.laboulangerie.laboulangeriemmo.betonquest;

import java.util.UUID;

import org.bukkit.Bukkit;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

public class XpEvent extends QuestEvent {

    public XpEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    @Override
    protected Void execute(String playerID) throws QuestRuntimeException {
        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager()
            .getPlayer(Bukkit.getPlayer(UUID.fromString(playerID)));
        if (mmoPlayer == null) return null; //Shouldn't happen but just in case

        String talentName = "";
        String rawValue = "";
        try {
            talentName = instruction.getPart(1);
            rawValue = instruction.getPart(2);
        } catch (InstructionParseException e) {
            e.printStackTrace();
        }
        double value = 0;
        try {
            value = Double.parseDouble(rawValue.substring(1));
        } catch (NumberFormatException error) {
            throw new QuestRuntimeException("Unable to parse given value to a decimal number", error);
        }
        switch (rawValue.charAt(0)) {
            case '+':
                mmoPlayer.getTalent(talentName).incrementXp(value);
                break;
            case '-':
                mmoPlayer.getTalent(talentName).decrementXp(value);
                break;
            default:
                throw new QuestRuntimeException("Unknown operation: "+rawValue.charAt(0)+", should be either '+' or '-'");
        }
        return null;
    }
    
}
