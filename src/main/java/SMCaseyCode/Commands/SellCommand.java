package SMCaseyCode.Commands;

import SMCaseyCode.DatabaseManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SellCommand {

    DatabaseManager db = new DatabaseManager();

    public void sellCommandEvent(SlashCommandInteractionEvent event) {
        String symbol = event.getOption("symbol").getAsString().toUpperCase();
        int qty = event.getOption("qty").getAsInt();

        if (qty <= 0){
            event.reply("Can't sell less than 1 share").setEphemeral(true).queue();
        } else {
            //double price = api.alpacaGetTrade(symbol).getP();
            double price = db.getStockPrice(symbol);

            int sold = db.sellSymbol(event.getUser().getId(), symbol, qty);

            if (sold > 0){
                event.reply(event.getMember().getAsMention() + " sold " + sold + " of " + symbol + " @ $" + price + " per share").queue();
            }else if (sold == 0){
                event.reply(event.getMember().getAsMention() + " does not own enough of symbol " + symbol).queue();
            }else if (sold == -2){
                event.reply("Symbol not found").queue();
            }else {
                event.reply("Transaction Failed. Please try again.").queue();
            }
        }

    }
}
