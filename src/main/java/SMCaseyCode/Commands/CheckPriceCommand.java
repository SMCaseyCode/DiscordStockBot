package SMCaseyCode.Commands;

import SMCaseyCode.AlpacaManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.historical.trade.Trade;

public class CheckPriceCommand {

    AlpacaManager api = new AlpacaManager();

    //Checks current price of a symbol
    public void checkPriceCommandEvent(SlashCommandInteractionEvent event) {
        OptionMapping messageOption = event.getOption("symbol");
        String symbol = messageOption.getAsString().toUpperCase();

        Trade stock = api.alpacaGetTrade(symbol);
        if (stock != null){
            event.reply(symbol + " is currently at $" + stock.getP() + " per share").queue();
        } else {
            event.reply(symbol + " could not be found.").queue();
        }

    }
}
