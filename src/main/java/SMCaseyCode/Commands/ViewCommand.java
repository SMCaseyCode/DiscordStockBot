package SMCaseyCode.Commands;

import SMCaseyCode.AlpacaManager;
import SMCaseyCode.DatabaseManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
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


        if (positions.size() == 0){
            event.reply(event.getMember().getAsMention() + " does not currently hold any positions.").queue();
        }else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(event.getUser().getName() + "'s Portfolio", "https://www.youtube.com/watch?v=1FFBsX5C61Q",event.getUser().getAvatarUrl());
            embed.setFooter("ID: " + userID);
            for (int i = 0; i < positions.size(); i++){
                qty = Integer.parseInt(positions.get(i + 1));
                individualPrice = api.alpacaGetTrade(positions.get(i)).getP();
                totalStockWorth += individualPrice * qty;
                i += 2;
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

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        DecimalFormat df = new DecimalFormat("0.00");

        if (event.getButton().getId().equals("positions")){
            EmbedBuilder embed = new EmbedBuilder();

            //Grabs userID from embed
            String userID = event.getMessage().getEmbeds().get(0).getFooter().getText().replace("ID: ", "");

            List<String> positions = db.viewPortfolio(userID);
            List<MessageEmbed> embeds = new ArrayList<>();
            int maxLimit = 12;
            boolean posted = false;
            int qty;
            double individualPrice;
            String name = bot.getUserById(userID).getName();
            String url = bot.getUserById(userID).getAvatarUrl();

            embed.setAuthor( name + "'s Portfolio", "https://www.youtube.com/watch?v=1FFBsX5C61Q", url);
            embed.setColor(Color.MAGENTA);

            for (int i = 0; i < positions.size(); i++){

                qty = Integer.parseInt(positions.get(i + 1));
                individualPrice = api.alpacaGetTrade(positions.get(i)).getP();

                //Grabs totalSpent from portfolio table
                double totalCost = Double.parseDouble(positions.get(i + 2));

                //Calculate totalReturn on investment
                double totalReturn = (((individualPrice * qty) - totalCost)/totalCost) * 100;

                embed.addField(positions.get(i), "", false);
                embed.addField("$" + individualPrice, " per share",true);
                embed.addField(String.valueOf(qty), "qty", true);

                //Dictates where return field has '+' or '-'
                if (totalReturn >= 0){
                    df.setRoundingMode(RoundingMode.CEILING);
                    embed.addField( "+" + df.format(totalReturn) + "%", "return", true);
                }else {
                    df.setRoundingMode(RoundingMode.FLOOR);
                    embed.addField( "-" + df.format(totalReturn) + "%", "return", true);
                }
                embed.addField("-----------------------------", "", false);

                //Moves around list as needed
                i += 2;

                //Post to Discord (required due to embed limits)
                if (i == maxLimit - 1 && !posted){
                    event.deferReply(true).queue();
                    embeds.add(embed.build());
                    posted = true;
                    maxLimit += 12;
                    embed = new EmbedBuilder();
                } else if (i == maxLimit - 1 || i + 1 == positions.size()) {
                    embed.setColor(Color.MAGENTA);
                    embeds.add(embed.build());
                    embed = new EmbedBuilder();
                }
            }

            if (posted){
                // Posts if user holds 4 or more symbols
                event.getHook().sendMessageEmbeds(embeds).queue();
            }else {
                // Posts if user holds 3 or fewer symbols
                event.replyEmbeds(embeds).setEphemeral(true).queue();
            }

        }
    }

    public static void getBot(JDA API){
        bot = API;
    }

    static private JDA bot;

}
