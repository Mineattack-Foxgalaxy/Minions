package io.github.skippyall.minions.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.skippyall.minions.Minions;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.HashSet;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TestSubcommand {
    public static LiteralArgumentBuilder<ServerCommandSource> TEST = literal("test")
            .then(argument("pos", BlockPosArgumentType.blockPos())
                    .executes(TestSubcommand::execute)
            );


    private static int execute(CommandContext<ServerCommandSource> context) {
        try {
            BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
            Collection<BlockPos> result = findInputs(context.getSource().getWorld(), pos);
            for (BlockPos resultPos : result) {
                context.getSource().sendFeedback(() -> Text.literal(resultPos.toShortString()), false);
            }
        } catch (Throwable e) {
            Minions.LOGGER.error("Error", e);
        }
        return 0;
    }

    private static Collection<BlockPos> findInputs(World world, BlockPos pos) {
        //positions that are already processed
        Collection<BlockPos> visitedPositions = new HashSet<>();
        //positions we are currently looking at
        Collection<BlockPos> currentPositions = new HashSet<>();
        //new positions are added here
        Collection<BlockPos> newPositions = new HashSet<>();
        //resulting machines
        Collection<BlockPos> machines = new HashSet<>();

        currentPositions.add(pos);

        while(!currentPositions.isEmpty()) {
            for(BlockPos currentPosition : currentPositions) {
                for(Direction dir : Direction.values()) {
                    //check each neighbor of the current positions
                    BlockPos newPos = currentPosition.offset(dir);
                    //Do not check blocks that were already checked
                    if(!visitedPositions.contains(newPos)) {
                        if (world.getBlockState(newPos).getBlock() == Blocks.REDSTONE_BLOCK) {
                            //Add pipes to positions for the next iteration
                            newPositions.add(newPos);
                        } else if (world.getBlockState(newPos).getBlock() == Blocks.IRON_BLOCK) {
                            //Add machines to output set
                            machines.add(newPos);
                        }
                        //Add checked blocks here so that they are not checked again
                        visitedPositions.add(newPos);
                    }
                }
            }
            //Check the new positions in the next iteration
            currentPositions = newPositions;
            newPositions = new HashSet<>();
        }

        return machines;
    }
}
