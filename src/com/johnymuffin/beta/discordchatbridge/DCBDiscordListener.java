package com.johnymuffin.beta.discordchatbridge;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Random;

public class DCBDiscordListener extends ListenerAdapter {
    private DiscordChatBridge plugin;

    public DCBDiscordListener(DiscordChatBridge plugin) {
        this.plugin = plugin;
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //Don't respond to bots
        if (event.getAuthor().isBot() || event.getAuthor().isFake()) {
            return;
        }
        //Don't respond to funky messages
        if (event.getMessage().getContentRaw().isEmpty()) {
            return;
        }

        String gameBridgeChannelID = plugin.getConfig().getConfigString("channel-id");
        String[] messageCMD = event.getMessage().getContentRaw().split(" ");
        //Is the message in the game bridge channel
        if (event.getChannel().getId().equalsIgnoreCase(gameBridgeChannelID)) {
            String displayName;
            if (event.getMember().getNickname() != null) {
                displayName = event.getMember().getNickname();
            } else {
                displayName = event.getAuthor().getName();
            }

            String chatMessage = plugin.getConfig().getConfigString("message.discord-chat-message");
            chatMessage = chatMessage.replace("%messageAuthor%", displayName);
            chatMessage = chatMessage.replace("%message%", event.getMessage().getContentDisplay());
            chatMessage = chatMessage.replaceAll("(&([a-f0-9]))", "\u00A7$2");
            Bukkit.broadcastMessage(chatMessage);
            return;
        }
        if(messageCMD[0].equalsIgnoreCase("!online") && plugin.getConfig().getConfigBoolean("online-command-enabled")) {
            String onlineMessage = "**The online players are:** ";
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                onlineMessage += p.getName() + ", ";
            }
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(plugin.getConfig().getConfigString("server-name") + " Online Players", null);
            if (Bukkit.getServer().getOnlinePlayers().length > 0) {
                int rnd = new Random().nextInt(Bukkit.getServer().getOnlinePlayers().length);
                Player player = Bukkit.getServer().getOnlinePlayers()[rnd];
                eb.setThumbnail("http://minotar.net/helm/" + player.getName() + "/100.png");
            }
            eb.setColor(Color.red);
            eb.setDescription("There are currently **" + Bukkit.getServer().getOnlinePlayers().length
                    + "** players online\n" + onlineMessage);
            eb.setFooter("https://github.com/RhysB/Discord-Bot-Chatbridge",
                    "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png");

            event.getChannel().sendMessage(eb.build()).queue();

        }


    }


}