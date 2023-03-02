package SMCaseyCode.Commands;

import SMCaseyCode.AlpacaManager;
import SMCaseyCode.DatabaseManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewCommand extends ListenerAdapter {

    DatabaseManager db = new DatabaseManager();
    AlpacaManager api = new AlpacaManager();

    public void viewCommandEvent(SlashCommandInteractionEvent event) {
        String userID = event.getUser().getId();
        List<String> positions = db.viewPortfolio(userID);
        int qty;
        double individualPrice;
        double totalStockWorth = 0;

        double walletBalance = db.checkWallet(event.getUser().getId());

        DecimalFormat df = new DecimalFormat("0.00");


        if (positions == null){
            event.reply(event.getMember().getAsMention() + " does not currently hold any positions.").queue();
        }else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(event.getUser().getName() + "'s Portfolio", "https://www.youtube.com/watch?v=1FFBsX5C61Q",event.getUser().getAvatarUrl());

            for (int i = 0; i < positions.size(); i++){
                qty = Integer.parseInt(positions.get(i + 1));
                individualPrice = api.alpacaGetTrade(positions.get(i)).getP();
                totalStockWorth += individualPrice * qty;
                i++;
            }

            embed.addField("Balance: ", "$" + df.format(walletBalance), false);
            embed.addField("Position Value: ", "$" + df.format(totalStockWorth), false);
            double totalReturn = ((walletBalance + totalStockWorth - 10000)/10000) * 100;
            if (totalReturn >= 0){
                df.setRoundingMode(RoundingMode.CEILING);
                embed.setColor(Color.green);
            }else {
                df.setRoundingMode(RoundingMode.FLOOR);
                embed.setColor(Color.red);
            }
            embed.addField("Total Return: ", df.format(totalReturn) + "%", false);

            event.replyEmbeds(embed.build()).addActionRow(Button.primary("positions", "Show Positions")).queue();
        }
    }
        //TODO: get correct userID in onButtonInteraction.
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId().equals("positions")){
            EmbedBuilder embed = new EmbedBuilder();
            String userID = event.getUser().getId();
            List<String> positions = db.viewPortfolio(userID);
            List<MessageEmbed> embeds = new ArrayList<>();
            int maxLimit = 12;
            boolean posted = false;
            int qty;
            double individualPrice;

            embed.setAuthor(event.getUser().getName() + "'s Portfolio", "https://www.youtube.com/watch?v=1FFBsX5C61Q",event.getUser().getAvatarUrl());
            embed.setColor(Color.MAGENTA);

            for (int i = 0; i < positions.size(); i++){
                embed.addField(positions.get(i), "", true);
                qty = Integer.parseInt(positions.get(i + 1));
                individualPrice = api.alpacaGetTrade(positions.get(i)).getP();
                embed.addField("$" + individualPrice, " per share",true);
                embed.addField(String.valueOf(qty), "qty", true);
                embed.addBlankField(false);
                i++;

                if (i == maxLimit - 1 && !posted){
                    event.deferReply(true).queue();
                    embeds.add(embed.build());
                    posted = true;
                    maxLimit += 12;
                    embed = new EmbedBuilder();
                } else if (i == maxLimit - 1 || i + 1 == positions.size()) {
                    embed.setColor(Color.MAGENTA);
                    embeds.add(embed.build());
                }
            }

            event.getHook().sendMessageEmbeds(embeds).queue();

        }
    }

}
