package SMCaseyCode;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import SMCaseyCode.Commands.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    DatabaseManager db = new DatabaseManager();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        //Slash Command Event
        String command = event.getName();
        String userID = event.getUser().getId();

        //Checks if user is in db, if not, create entry
        db.checkUser(userID);

        //Command Classes
        BuyCommand bc = new BuyCommand();
        SellCommand sc = new SellCommand();
        ViewCommand vc = new ViewCommand();
        CheckPriceCommand cpc = new CheckPriceCommand();
        WalletCommand wc = new WalletCommand();
        ResetCommand rc = new ResetCommand();
        BuyMaxCommand bmc = new BuyMaxCommand();
        SellMaxCommand smc = new SellMaxCommand();
        LeaderboardCommand lbc = new LeaderboardCommand();
        HelpCommand hc = new HelpCommand();

        //Command Manager
        switch (command) {
            case "buy" -> bc.buyCommandEvent(event);
            case "sell" -> sc.sellCommandEvent(event);
            case "portfolioview" -> vc.viewCommandEvent(event);
            case "checkprice" -> cpc.checkPriceCommandEvent(event);
            case "wallet" -> wc.walletCommandEvent(event);
            case "resetaccount" -> rc.resetCommandEvent(event);
            case "buymax" -> bmc.buyMaxCommandEvent(event);
            case "sellmax" -> smc.sellMaxEvent(event);
            case "leaderboard" -> lbc.leaderboardCommandEvent(event);
            case "help" -> hc.helpCommandEvent(event);
        }

    }

    @Override
    public void onReady(ReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

        OptionData option = new OptionData(OptionType.STRING, "symbol", "stock symbol", true);
        OptionData option2 = new OptionData(OptionType.INTEGER, "qty", "qty to buy", true);
        commandData.add(Commands.slash("buy", "Buy a Stock").addOptions(option, option2));

        option = new OptionData(OptionType.STRING, "symbol", "stock symbol", true);
        option2 = new OptionData(OptionType.INTEGER, "qty", "qty to sell", true);
        commandData.add(Commands.slash("sell", "Sell a Stock").addOptions(option, option2));

        commandData.add(Commands.slash("portfolioview", "View your portfolio"));

        option = new OptionData(OptionType.STRING, "symbol", "stock symbol", true);
        commandData.add(Commands.slash("checkprice", "Check price of Stock").addOptions(option));

        commandData.add(Commands.slash("wallet", "Shows wallet balance"));

        option = new OptionData(OptionType.STRING, "confirm", "Type Confirm To Wipe", true);
        commandData.add(Commands.slash("resetaccount", "WARNING: RESETS ACCOUNT || TYPE CONFIRM").addOptions(option));

        option = new OptionData(OptionType.STRING, "symbol", "stock symbol", true);
        commandData.add(Commands.slash("sellmax", "Sells total position").addOptions(option));
        commandData.add(Commands.slash("buymax", "Purchases as many shares as possible").addOptions(option));

        commandData.add(Commands.slash("leaderboard", "Shows the biggest whales"));

        commandData.add(Commands.slash("help", "Use this for extra info!"));

        event.getJDA().updateCommands().addCommands(commandData).queue();

    }

}
