package io.github.skippyall.minions.program;

import io.github.skippyall.minions.program.statement.StatementList;
import io.github.skippyall.minions.program.variables.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinionRuntime implements Runnable{
    private boolean run = true;
    public final Map<String, Variable> variables = new HashMap<>();
    private final List<StatementList.Run> runs = new ArrayList<>();

    @Override
    public void run() {
        while(run) {

        }
    }

    public void stop() {
        run = false;
    }
}
