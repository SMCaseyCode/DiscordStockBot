package SMCaseyCode.Commands;

import SMCaseyCode.DatabaseManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SellMaxCommand {

    DatabaseManager db = new DatabaseManager();

    public void sellMaxEvent(SlashCommandInteractionEvent event){
        String userID = event.getUser().getId();
        String symbol = event.getOption("symbol").getAsString().toUpperCase();

        int qty = db.checkOwnership(userID, symbol);

        if (qty > 0){
            double price = db.getStockPrice(symbol);
            db.sellSymbol(userID, symbol, qty);
            event.reply(event.getUser().getAsMention() + " has sold " + qty + " of " + symbol + " @ $" + price + " per share.").queue();
        }else {
            event.reply("You do not own any of " + symbol).setEphemeral(true).queue();
        }

    }
}
