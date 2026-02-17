package io.github.skippyall.minions.websocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.consumer.ValueConsumerList;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.ConfiguredInstructionListener;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.supplier.FixedValueSupplier;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.registration.ValueSuppliers;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MinionWebsocketManager {
    private static Map<UUID, MinionWebsocketManager> managers;

    private int currentRequestId = 0;
    private final Map<Integer, WebsocketRequest> requests = new ConcurrentHashMap<>();
    private MinionFakePlayer minion;

    public MinionWebsocketManager(MinionFakePlayer minion) {
        this.minion = minion;
    }

    public static MinionWebsocketManager get(UUID minion) {
        return managers.get(minion);
    }

    public WebsocketRequest getRequest(int id) {
        return requests.get(id);
    }

    public void handleMessage(JsonObject msg) {
        String type = msg.get("type").getAsString();
        if(type.equals("run_instruction")) {
            onRunInstruction(msg);
        }
    }

    private void onRunInstruction(JsonObject msg) {
        Identifier id = Identifier.tryParse(msg.get("instruction").getAsString());
        InstructionType<MinionRuntime> instructionType = MinionRegistries.INSTRUCTION_TYPES.get(id);

        if(instructionType == null) {
            return;
        }

        ValueSupplierList<MinionRuntime> valueSuppliers = new ValueSupplierList<>();
        JsonObject arguments = msg.get("arguments").getAsJsonObject();
        for(String argumentName : arguments.keySet()) {
            JsonElement argument = arguments.get(argumentName);

            Parameter<?> parameter = null;
            for(Parameter<?> otherParameter : instructionType.getParameters()) {
                if(otherParameter.name().equals(argumentName)) {
                    parameter = otherParameter;
                    break;
                }
            }
            if(parameter != null) {
                addArgument(valueSuppliers, argument, parameter);
            }
        }
        currentRequestId++;
        int requestId = currentRequestId;

        ValueConsumerList<MinionRuntime> valueConsumers = new ValueConsumerList<>();
        for(Parameter<?> parameter : instructionType.getReturnParameters()) {
            valueConsumers.setValueConsumer(parameter, new WebsocketValueConsumer<>(parameter.type(), parameter.name(), requestId));
        }

        ConfiguredInstruction<MinionRuntime> instruction = new ConfiguredInstruction<>(instructionType, valueSuppliers, new ValueConsumerList<>(), null);
    }

    public <T> void addArgument(ValueSupplierList<MinionRuntime> valueSuppliers, JsonElement element, Parameter<T> parameter) {
        if(parameter.type() != null) {
            Optional<T> value = parameter.type().codec().decode(JsonOps.INSTANCE, element).map(Pair::getFirst).result();
            if(value.isPresent()) {
                valueSuppliers.setArgument(parameter, new FixedValueSupplier<>(ValueSuppliers.FIXED_VALUE_SUPPLIER_TYPE, parameter.type(), value.get()));
            }
        }
    }

    public class WebsocketRequest implements ConfiguredInstructionListener {
        private JsonObject returnObject = null;
        private ConfiguredInstruction<MinionRuntime> instruction;

        public WebsocketRequest(ConfiguredInstruction<MinionRuntime> instruction) {
            this.instruction = instruction;
            instruction.addListener(this);
        }

        public void acceptReturnValue(String key, JsonElement value) {
            if(returnObject == null) {
                returnObject = new JsonObject();
            }
            returnObject.add(key, value);
        }
    }
}
