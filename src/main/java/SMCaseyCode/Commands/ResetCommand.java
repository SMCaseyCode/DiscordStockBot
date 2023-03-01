package SMCaseyCode.Commands;

import SMCaseyCode.DatabaseManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ResetCommand {

    DatabaseManager db = new DatabaseManager();

    public void resetCommandEvent(SlashCommandInteractionEvent event) {
        boolean success = db.resetAccount(event.getUser().getId(), event.getOption("confirm"));

        if (success){
            event.reply(event.getMember().getAsMention() + " has been reset successfully").queue();
        }else {
            event.reply("User reset has failed.").queue();
        }

    }
}
