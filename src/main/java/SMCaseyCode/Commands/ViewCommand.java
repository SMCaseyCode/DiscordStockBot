package SMCaseyCode.Commands;

import SMCaseyCode.AlpacaManager;
import SMCaseyCode.DatabaseManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class ViewCommand {

    DatabaseManager db = new DatabaseManager();
    AlpacaManager api = new AlpacaManager();

    public void viewCommandEvent(SlashCommandInteractionEvent event) {
        List<String> positions = db.viewPortfolio(event.getUser().getId());
        int qty;
        double totalStockWorth = 0;
        double walletBalance = db.checkWallet(event.getUser().getId());

        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.CEILING);

        if (positions == null){
            event.reply(event.getMember().getAsMention() + " does not currently hold any positions.").queue();
        }else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.MAGENTA);
            embed.setTitle(event.getUser().getName() + "'s Portfolio");

            for (int i = 0; i < positions.size(); i++){
                embed.addField(positions.get(i), "Owned: " + positions.get(i + 1), false);
                qty = Integer.parseInt(positions.get(i + 1));
                totalStockWorth += api.alpacaGetTrade(positions.get(i)).getP() * qty;
                i++;
            }

            embed.addField("Balance: ", "$" + df.format(walletBalance), true);
            embed.addField("Position Value: ", "$" + df.format(totalStockWorth), true);
            embed.addField("Total Worth: ", "$" + df.format(walletBalance + totalStockWorth), true);

            event.replyEmbeds(embed.build()).queue();
        }
    }
}
