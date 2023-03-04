package SMCaseyCode.Commands;

import SMCaseyCode.AlpacaManager;
import SMCaseyCode.DatabaseManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class BuyMaxCommand {

    DatabaseManager db = new DatabaseManager();
    AlpacaManager api = new AlpacaManager();
    public void buyMaxCommandEvent(SlashCommandInteractionEvent event){
        String userID = event.getUser().getId();
        String symbol = event.getOption("symbol").getAsString().toUpperCase();
        double wallet = db.checkWallet(userID);
        double cost = api.alpacaGetTrade(symbol).getP();

        int qty = (int) (wallet/cost);

        if (qty >= 1){
            db.buySymbol(userID, qty, symbol);
            event.reply(event.getUser().getAsMention() + " has bought " + qty + " of " + symbol).queue();
        }else {
            event.reply("You can not afford any of " + symbol).setEphemeral(true).queue();
        }

    }
}
