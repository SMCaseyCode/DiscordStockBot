package SMCaseyCode.Commands;

import SMCaseyCode.AlpacaManager;
import SMCaseyCode.DatabaseManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SellCommand {

    DatabaseManager db = new DatabaseManager();
    AlpacaManager api = new AlpacaManager();

    public void sellCommandEvent(SlashCommandInteractionEvent event) {
        String symbol = event.getOption("symbol").getAsString().toUpperCase();
        int qty = event.getOption("qty").getAsInt();
        double price = api.alpacaGetTrade(symbol).getP();

        int sold = db.sellSymbol(event.getUser().getId(), symbol, qty);

        if (sold > 0){
            event.reply(event.getMember().getAsMention() + " sold " + sold + " of " + symbol + " @ $" + price + " per share").queue();
        }else if (sold == 0){
            event.reply(event.getMember().getAsMention() + " does not own enough of symbol " + symbol).queue();
        }else if (sold == -2){
            event.reply("Symbol not found").queue();
        }else {
            event.reply("UNKNOWN ERROR. PLEASE REPORT TO DOCSNIPE#0101").queue();
        }

    }
}
