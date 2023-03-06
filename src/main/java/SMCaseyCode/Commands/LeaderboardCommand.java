package SMCaseyCode.Commands;

import SMCaseyCode.DatabaseManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LeaderboardCommand {

    DatabaseManager db = new DatabaseManager();

    public void leaderboardCommandEvent(SlashCommandInteractionEvent event){

        EmbedBuilder embed = new EmbedBuilder();

        List<String> userList = db.getUserPositionWorth();
        String[] keySet = new String[userList.size() / 2];
        String[] valueSet = new String[userList.size() / 2];
        int counter = 0;
        int index = 0;

        for (int i = 0; i < userList.size() - 1; i++){

            keySet[index] =  userList.get(counter);
            counter += 2;
            i++;
            valueSet[index] = userList.get(i);
            index++;
        }

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
