package com.pajilabs.tpneoforge;

import java.util.logging.Logger;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@EventBusSubscriber(modid = tpneoforge.MODID, value = Dist.DEDICATED_SERVER)
public class TeleportCommand {
    private static final Logger LOGGER = Logger.getLogger(TeleportCommand.class.getName());

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        // log to console
        LOGGER.info("Registering teleport command");
        event.getServer().getCommands().getDispatcher().register(
            Commands.literal("tp")
                .then(Commands.literal("@s")
                    .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                        .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                            .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                .executes(ctx -> teleportSelf(ctx)))))));
    }

    private static int teleportSelf(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("This command can only be executed by a player!"));
            return 0;
        }
        double x = DoubleArgumentType.getDouble(ctx, "x");
        double y = DoubleArgumentType.getDouble(ctx, "y");
        double z = DoubleArgumentType.getDouble(ctx, "z");
        player.teleportTo(x, y, z);
        ctx.getSource().sendSuccess(() -> Component.literal("Teleported to " + x + ", " + y + ", " + z), true);
        return 1;
    }
}