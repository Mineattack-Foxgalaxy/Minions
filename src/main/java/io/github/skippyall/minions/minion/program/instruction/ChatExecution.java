package io.github.skippyall.minions.minion.program.instruction;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.registration.ExecutionContext;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;

public class ChatExecution implements InstructionExecution {
    public static final Parameter<String> MESSAGE = new Parameter<>("message", ValueTypes.STRING);
    public static final Codec<ChatExecution> CODEC = Codec.STRING.xmap(ChatExecution::new, e -> e.message);

    private String message;

    public ChatExecution() {}

    public ChatExecution(String message) {
        this.message = message;
    }

    @Override
    public void start(Context context) {
        MinionFakePlayer minion = context.getOrThrow(ExecutionContext.MINION_KEY);

        PlayerChatMessage chatMessage = PlayerChatMessage.system(message);
        ChatDecorator decorator = minion.getServer().getChatDecorator();
        Component decorated = decorator.decorate(minion, chatMessage.decoratedContent());
        chatMessage = chatMessage.withUnsignedContent(decorated);

        CommandSourceStack sourceStack = minion.createCommandSourceStack();

        minion.getServer().getPlayerList().broadcastChatMessage(chatMessage, sourceStack, ChatType.bind(ChatType.SAY_COMMAND, sourceStack));
    }

    @Override
    public boolean isDone(Context context) {
        return true;
    }

    @Override
    public void readArguments(ParameterValueList arguments, Context context) {
        message = arguments.getValue(MESSAGE);
    }
}
