package io.github.skippyall.minions.program.conversion;

import io.github.skippyall.minions.program.value.ValueType;

public class Cast<F, T> {
    private final ValueType<F> from;
    private final ValueType<T> to;

    private final Caster<F,T> caster;

    private final boolean lossy, canFail;

    public Cast(ValueType<F> from, ValueType<T> to, Caster<F,T> caster) {
        this(from, to, caster, false, false);
    }

    private Cast(ValueType<F> from, ValueType<T> to, Caster<F,T> caster, boolean lossy, boolean canFail) {
        this.from = from;
        this.to = to;
        this.caster = caster;

        this.lossy = lossy;
        this.canFail = canFail;
    }

    public T cast(F from) {
        return caster.cast(from);
    }

    public ValueType<F> getFrom() {
        return from;
    }

    public ValueType<T> getTo() {
        return to;
    }

    public boolean isLossy() {
        return lossy;
    }

    public boolean canFail() {
        return canFail;
    }

    public static class CastCrafter<F, T> {
        private final ValueType<F> from;
        private final ValueType<T> to;

        private final Caster<F,T> caster;

        private boolean lossy, canFail;

        public CastCrafter(ValueType<F> from, ValueType<T> to, Caster<F, T> caster) {
            this.from = from;
            this.to = to;
            this.caster = caster;
        }

        public CastCrafter<F, T> lossy() {
            lossy = true;
            return this;
        }

        public CastCrafter<F, T> canFail() {
            canFail = true;
            return this;
        }

        public Cast<F, T> craftCast() {
            return new Cast<>(from, to, caster, lossy, canFail);
        }
    }

    @FunctionalInterface
    public interface Caster<F, T> {
        T cast(F from);
    }
}
