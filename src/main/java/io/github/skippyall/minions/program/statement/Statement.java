package io.github.skippyall.minions.program.statement;

import io.github.skippyall.minions.program.argument.ArgUtils;
import io.github.skippyall.minions.program.block.CodeBlock;
import io.github.skippyall.minions.program.runtime.ProgramRuntime;
import io.github.skippyall.minions.program.tuple.Tuple;
import io.github.skippyall.minions.program.variables.ValueStorage;

public record Statement<R,A extends Tuple>(CodeBlock<R,A> codeBlock, A args, ValueStorage<R> valueStorage) {
    @SuppressWarnings("unchecked")
    public Run start(ProgramRuntime runtime) {
        Run run = new Run(runtime);
        codeBlock.start(runtime, (A) ArgUtils.resolveArgs(args, runtime), run);
        return run;
    }

    public class Run {
        private boolean running = false;
        private boolean waiting = false;
        private boolean afterRun = false;
        private R cachedResult;
        private int ticksRunning = 0;
        private int ticksLeft = 0;

        private final ProgramRuntime runtime;

        Run(ProgramRuntime runtime) {
            this.runtime = runtime;
        }

        public boolean isDone() {
            return !running;
        }

        public void tick() {
            if(running) {
                ticksRunning ++;
                ticksLeft --;
                if(ticksLeft == 0 && waiting && afterRun) {
                    complete(cachedResult);
                }
            }
        }

        public void afterRun(R result) {
            afterRun = true;
            if(ticksLeft == 0) {
                complete(result);
            }
        }

        private void complete(R result) {
            if(valueStorage != null) {
                valueStorage.storeValue(result, runtime);
            }
            running = false;
            waiting = false;
            cachedResult = null;
            ticksRunning = 0;
            ticksLeft = 0;
        }

        public void completeAfter(int ticks, R value) {
            waiting = true;
            ticksLeft = ticks;
            cachedResult = value;
        }

        public int getTicksRunning() {
            return ticksRunning;
        }
    }
}
