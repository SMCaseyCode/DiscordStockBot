package SMCaseyCode.Commands;

import SMCaseyCode.DatabaseManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

public class LeaderboardCommand {

    DatabaseManager db = new DatabaseManager();

    public void leaderboardCommandEvent(SlashCommandInteractionEvent event){

        EmbedBuilder embed = new EmbedBuilder();

        HashMap<String, Double> userMap = db.getUserPositionWorth();
        String[] keySet = Arrays.toString(userMap.keySet().toArray()).replace('[', ' ').replace(']', ' ').split(",");
        String[] valueSet = Arrays.toString(userMap.values().toArray()).replace('[', ' ').replace(']', ' ').split(",");
        String[] nameArray = new String[keySet.length];

        for (int i = 0; i < keySet.length; i++){
            nameArray[i] = "`" + (i + 1) + "` - " + bot.getUserById(keySet[i].trim()).getName();
            valueSet[i] = "$" + valueSet[i];
        }

        embed.setColor(Color.YELLOW);
        embed.setTitle(":tada: Top Stock Enjoyers: ");
        embed.addField("Top 10", Arrays.toString(nameArray).replace(',', '\n').replace('[', ' ').replace(']', ' ').trim(), true);
        embed.addField("Portfolio Value", Arrays.toString(valueSet).replace(',', '\n').replace('[', ' ').replace(']', ' ').trim(), true);

        event.replyEmbeds(embed.build()).queue();
    }

    public static void getBot(JDA API){
        bot = API;
    }

    static private JDA bot;
}
