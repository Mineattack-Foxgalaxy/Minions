package io.github.skippyall.minions.program.statement;

import io.github.skippyall.minions.program.runtime.ProgramRuntime;

import java.util.Iterator;
import java.util.List;

public class StatementList {
    private final List<Statement<?,?>> statements;

    public StatementList() {
        this.statements = List.of();
    }

    public StatementList(List<Statement<?,?>> statements) {
        this.statements = List.copyOf(statements);
    }

    public Run start(ProgramRuntime runtime) {
        return new Run(runtime);
    }

    public class Run {
        final ProgramRuntime runtime;
        final Iterator<Statement<?,?>> iterator;
        Statement<?,?>.Run run;

        public Run(ProgramRuntime runtime) {
            this.runtime = runtime;
            iterator = statements.iterator();
            startNext();
        }

        private boolean startNext() {
            if(iterator.hasNext()) {
                run = iterator.next().start(runtime);
                return true;
            } else {
                return false;
            }
        }

        public void tick() {
            run.tick();
            if(run.isDone()){
                startNext();
            }
        }
    }
}
