package SMCaseyCode;

import SMCaseyCode.Commands.ViewCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import static SMCaseyCode.Commands.ViewCommand.getBot;
import static SMCaseyCode.ProtectedData.TOKEN;

public class Bot_Run {

    public static void main(String[] args) {
        JDA bot = JDABuilder.createLight(TOKEN.getContent(), GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("Stonks"))
                .build();

        //EventManager.java
        registerEventListener(bot);
        registerButtonListener(bot);
        getBot(bot);

    }


    private static void registerEventListener(JDA api) {
        api.addEventListener(new CommandManager());
    }

    private static void registerButtonListener(JDA api){
        api.addEventListener(new ViewCommand());
    }
}
