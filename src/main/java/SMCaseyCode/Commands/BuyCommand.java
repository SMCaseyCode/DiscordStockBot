package SMCaseyCode.Commands;

import SMCaseyCode.DatabaseManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class BuyCommand {

    DatabaseManager db = new DatabaseManager();

    public void buyCommandEvent(SlashCommandInteractionEvent event) {
        String symbol = event.getOption("symbol").getAsString().toUpperCase();
        int qty = event.getOption("qty").getAsInt();

        if (qty > 0){
            int total = db.buySymbol(event.getUser().getId(), qty, symbol);

            if (total >= 0){
                event.reply(event.getMember().getAsMention() + " now owns " + total + " of symbol " + symbol).queue();
            } else if (total == -69) {
                event.reply("The symbol " + event.getOption("symbol").getAsString().toUpperCase() + " could not be found.").queue();
            } else {
                event.reply(event.getMember().getAsMention() + " does not have sufficient balance to carry out this transaction").queue();
            }
        }else {
            event.reply("Can't purchase less than 1 share").setEphemeral(true).queue();
        }

    }
}
