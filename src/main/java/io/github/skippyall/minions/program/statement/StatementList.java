package io.github.skippyall.minions.program.statement;

import java.util.ArrayList;
import java.util.List;

public class StatementList {
    private final List<Statement> statements;
    private boolean finish;

    StatementList() {
        this(new ArrayList<>());
    }

    StatementList(List<Statement> statements) {
        this.statements = statements;
    }

    public void runNext(Run run) {

    }

    public boolean canRunNext(Run run) {
        return true;
    }

    public boolean isFinish(Run run) {
        return finish;
    }

    public Run createRunInstance() {
        return null;
    }

    public class Run {

    }
}
