package SMCaseyCode;

import SMCaseyCode.Commands.LeaderboardCommand;
import SMCaseyCode.Commands.ViewCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static SMCaseyCode.Commands.ViewCommand.getBot;
import static SMCaseyCode.ProtectedData.TOKEN;

public class Bot_Run {

    static DatabaseManager db = new DatabaseManager();
    public static void main(String[] args) {
        JDA bot = JDABuilder.createLight(TOKEN.getContent(), GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("Stonks"))
                .build();

        db.initialDataInsert();

        //EventManager.java
        registerEventListener(bot);
        registerButtonListener(bot);
        getBot(bot);
        LeaderboardCommand.getBot(bot);

        //Auto-runs autoUpdater + autoReducer
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(Bot_Run::autoUpdater, 60, 60, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(Bot_Run::autoReducer, 45, 45, TimeUnit.SECONDS);

    }

    //Auto-updates stock prices every minute. Needed due to API limits on free tier. Even then, it is limited
    private static void autoUpdater() {
        db.updateStockData();
    }

    //Reduces the current_stock_data table to compensate for limited API
    private static void autoReducer() {
        db.dataReduce();
    }


    private static void registerEventListener(JDA api) {
        api.addEventListener(new CommandManager());
    }

    private static void registerButtonListener(JDA api){
        api.addEventListener(new ViewCommand());
    }
}
