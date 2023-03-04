package SMCaseyCode.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HelpCommand {

    public void helpCommandEvent(SlashCommandInteractionEvent event){
        EmbedBuilder embed = new EmbedBuilder();
        List<MessageEmbed> embedList = new ArrayList<>();

        embed.setTitle(":ledger: StockBot Information", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        embed.setColor(Color.GRAY);
        embed.addField("**What is my purpose? **", "Use me to simulate the US stock market! It is all fake" +
                " money so have fun. Of course, there is a leaderboard, try to get to the top!", false);
        embedList.add(embed.build());

        embed = new EmbedBuilder();

        embed.setTitle(":scroll: Available Commands");
        embed.setColor(Color.GRAY);
        embed.addField("/help: ", "You found this already! :smile:", false);
        embed.addField("/wallet: ", "Shows you how much USD you hold (everyone starts with 10k!)", false);
        embed.addField("/buy: ", "Purchase a stock using its ticker (ex: AMD)", false);
        embed.addField("/sell: ", "Sell a stock using its ticker (ex: NVDA)", false);
        embed.addField("/buymax: ", "Purchases as many of selected stock as possible", false);
        embed.addField("/sellmax: ", "Sells as many of a single stock as you hold", false);
        embed.addField("/checkprice: ", "Checks price of a stock", false);
        embed.addField("/portfolioview: ", "Shows your portfolio", false);
        embed.addField("/resetaccount: ", "RESETS portfolio + wallet with confirmation", false);
        embed.addField("/leaderboard: ", "Shows the top 10 whales among all users", false);
        embedList.add(embed.build());

        embed = new EmbedBuilder();

        embed.setTitle(":nerd: Support");
        embed.setColor(Color.GRAY);
        embed.addField("Discord: ", "DocSnipe#0101", false);
        embed.addField("Github: ", "https://github.com/SMCaseyCode", false);

        embedList.add(embed.build());

        event.replyEmbeds(embedList).queue();
    }
}
