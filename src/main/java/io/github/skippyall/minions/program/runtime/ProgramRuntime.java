package io.github.skippyall.minions.program.runtime;

import io.github.skippyall.minions.program.statement.StatementList;

import java.util.HashMap;
import java.util.Map;

public abstract class ProgramRuntime {
    private final Map<String, Object> variables = new HashMap<>();
    private StatementList statements;
    private StatementList.Run run;

    public ProgramRuntime() {
        this(new StatementList());
    }

    public ProgramRuntime(StatementList statements) {
        this.statements = statements;
    }

    public void start() {
        run = statements.start(this);
    }

    public void tick() {
        if(run != null) {
            run.tick();
        }
    }

    public StatementList getStatementList() {
        return statements;
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }

    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }
}
