package alpvax.advancedautocrafting.command;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.manager.NodeManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

public class NetworkCommand {
  public static void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(
        Commands.literal("craftnetwork")
            .then(Commands.literal("list")
                .then(Commands.literal("nodes")
                    .executes((ctx) -> {
                      return listNodes(ctx);//TODO: allow from server, add args etc.
                    })
                ).then(Commands.literal("networks")
                    .executes((ctx) -> {
                      return listNets(ctx);//TODO: allow from server, add args etc.
                    })
                )
            )
    );
  }

  private static int listNets(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
    CommandSource source = ctx.getSource();
    ServerPlayerEntity player = source.asPlayer();
    NodeManager manager = NodeManager.get(player.world, player.getPosition());
    ITextComponent text = new StringTextComponent(String.format("Networks in chunk{%d, %d}:\n", player.chunkCoordX, player.chunkCoordZ));
    manager.getNodes().values().stream().filter(Objects::nonNull).forEach(network -> {
      text.appendSibling(network.chatNetworkDisplay(true));
    });
    source.sendFeedback(text, false);
    return 0;
  }

  private static int listNodes(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
    CommandSource source = ctx.getSource();
    ServerPlayerEntity player = source.asPlayer();
    NodeManager manager = NodeManager.get(player.world, player.getPosition());
    Set<INetworkNode> nodes = manager.getNodes().keySet();
    ITextComponent text = new StringTextComponent(String.format("Nodes in chunk{%d, %d} (total %d):", player.chunkCoordX, player.chunkCoordZ, nodes.size()));
    nodes.stream()
        // Hide nodes which only provide connections
        //TODO: Config/command filter
        //.filter(n -> n.getFunctionalities().stream().anyMatch(nf -> nf != NodeFunctionality.EXTENDED_CONNECT))
        // Order by position
        .sorted(Comparator.comparing(INetworkNode::getPos))
        .forEachOrdered(node -> {
          BlockPos pos = node.getPos();
          text.appendText("\n  ")
              .appendSibling(node.getName())
              .appendText(String.format(" @ (%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ()));
        });
    source.sendFeedback(text, false);
    return 0;
  }
}
