package SMCaseyCode.Commands;

import SMCaseyCode.DatabaseManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class WalletCommand {

    DatabaseManager db = new DatabaseManager();

    public void walletCommandEvent(SlashCommandInteractionEvent event){
        double balance = db.checkWallet(event.getUser().getId());

        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.CEILING);

        event.reply(event.getMember().getAsMention() + " has $" + df.format(balance) + " in their wallet.").queue();
    }
}
