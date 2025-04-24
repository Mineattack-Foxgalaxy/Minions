package io.github.skippyall.minions.input;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Result<T, E> {
    static <T> Result<T, String> wrap(UnsafeOperation<T> toWrap) {
        return wrapCustomError(toWrap, Exception::getMessage);
    }

    static <T, E> Result<T, E> wrapCustomError(UnsafeOperation<T> toWrap, E error) {
        return wrapCustomError(toWrap,  e -> error);
    }

    static <T, E> Result<T, E> wrapCustomError(UnsafeOperation<T> toWrap, Function<Exception, E> errorTransformer) {
        try {
            return new Result.Success<>(toWrap.run());
        } catch (Exception e) {
            return new Result.Error<>(errorTransformer.apply(e));
        }
    }

    boolean isSuccess();

    @NotNull T getOrDefault(@NotNull T defaultValue);

    @NotNull T getOrThrow();

    @NotNull E getErrorOrThrow();

    @NotNull Optional<T> getOptional();

    void ifSuccess(@NotNull Consumer<T> handler);

    void ifError(@NotNull Consumer<Error<T, E>> handler);

    record Success<T, E>(@NotNull T result) implements Result<T, E> {
        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public @NotNull T getOrDefault(@NotNull T defaultValue) {
            return result;
        }

        @Override
        public @NotNull T getOrThrow() {
            return result;
        }

        @Override
        public @NotNull E getErrorOrThrow() {
            throw new RuntimeException("Result was not an Error");
        }

        @Override
        public @NotNull Optional<T> getOptional() {
            return Optional.of(result);
        }

        @Override
        public void ifSuccess(@NotNull Consumer<T> handler) {
            handler.accept(result);
        }

        @Override
        public void ifError(@NotNull Consumer<Error<T, E>> handler) {

        }
    }

    record Error<T, E>(@NotNull E message) implements Result<T, E> {
        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public @NotNull T getOrDefault(@NotNull T defaultValue) {
            return defaultValue;
        }

        @Override
        public @NotNull T getOrThrow() {
            throw new RuntimeException("Result was an error: " + message.toString());
        }

        @Override
        public @NotNull E getErrorOrThrow() {
            return message;
        }

        @Override
        public @NotNull Optional<T> getOptional() {
            return Optional.empty();
        }

        @Override
        public void ifSuccess(@NotNull Consumer<T> handler) {

        }

        @Override
        public void ifError(Consumer<Error<T, E>> handler) {
            handler.accept(this);
        }
    }

    interface UnsafeOperation<T> {
        T run() throws Exception;
    }
}
