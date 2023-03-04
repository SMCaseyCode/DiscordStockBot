package SMCaseyCode.Commands;

import SMCaseyCode.DatabaseManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SellMaxCommand {

    DatabaseManager db = new DatabaseManager();

    public void sellMaxEvent(SlashCommandInteractionEvent event){
        String userID = event.getUser().getId();
        String symbol = event.getOption("symbol").getAsString();

        int qty = db.checkOwnership(userID, symbol);

        if (qty > 0){
            db.sellSymbol(userID, symbol, qty);
            event.reply(event.getUser().getAsMention() + " has sold " + qty + " of " + symbol).queue();
        }else {
            event.reply("You do not own any of " + symbol).setEphemeral(true).queue();
        }

    }
}
