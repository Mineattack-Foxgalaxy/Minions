package io.github.skippyall.minions;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinionsTickExecutor {
    private static final List<Runnable> executeOnNextTick = Collections.synchronizedList(new ArrayList<>());

    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            synchronized (executeOnNextTick) {
                for (Runnable run:executeOnNextTick) {
                    run.run();
                }
                executeOnNextTick.clear();
            }
        });
    }

    public static void addExecuteOnNextTick(Runnable run) {
        executeOnNextTick.add(run);

    }
}
