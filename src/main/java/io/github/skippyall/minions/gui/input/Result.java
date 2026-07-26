package io.github.skippyall.minions.gui.input;

import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Result<T, E> permits Result.Success, Result.Error {
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

    static <T, E> Result<T, E> ofNullable(@Nullable T value, E error) {
        if(value != null) {
            return new Success<>(value);
        } else {
            return new Error<>(error);
        }
    }

    static <T, E> Result<T, E> ofNullable(@Nullable T value, Supplier<E> error) {
        if(value != null) {
            return new Success<>(value);
        } else {
            return new Error<>(error.get());
        }
    }

    boolean isSuccess();

    T getOrDefault(T defaultValue);

    T getOrThrow();

    E getErrorOrThrow();

    Optional<T> getOptional();

    Optional<E> getOptionalError();

    void ifSuccess(Consumer<T> handler);

    void ifError(Consumer<Error<T, E>> handler);

    <U> Result<U,E> map(Function<T, U> mapper);

    <U> Result<U,E> flatMap(Function<T, Result<U, E>> mapper);

    <U> Result<T,U> mapError(Function<E, U> mapper);

    record Success<T, E>(T result) implements Result<T, E> {
        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T getOrDefault(T defaultValue) {
            return result;
        }

        @Override
        public Optional<E> getOptionalError() {
            return Optional.empty();
        }

        @Override
        public T getOrThrow() {
            return result;
        }

        @Override
        public E getErrorOrThrow() {
            throw new RuntimeException("Result was not an Error");
        }

        @Override
        public Optional<T> getOptional() {
            return Optional.of(result);
        }

        @Override
        public void ifSuccess(Consumer<T> handler) {
            handler.accept(result);
        }

        @Override
        public void ifError(Consumer<Error<T, E>> handler) {

        }

        @Override
        public <U> Result<U, E> map(Function<T, U> mapper) {
            return new Success<>(mapper.apply(result));
        }

        @Override
        public <U> Result<U, E> flatMap(Function<T, Result<U, E>> mapper) {
            return mapper.apply(result);
        }

        @Override
        public <U> Result<T, U> mapError(Function<E, U> mapper) {
            return new Success<>(result);
        }
    }

    record Error<T, E>(E message) implements Result<T, E> {
        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T getOrDefault(
                T defaultValue) {
            return defaultValue;
        }

        @Override
        public T getOrThrow() {
            throw new RuntimeException("Result was an error: " + message);
        }

        @Override
        public E getErrorOrThrow() {
            return message;
        }

        @Override
        public Optional<T> getOptional() {
            return Optional.empty();
        }

        @Override
        public Optional<E> getOptionalError() {
            return Optional.of(message);
        }

        @Override
        public void ifSuccess(Consumer<T> handler) {

        }

        @Override
        public void ifError(Consumer<Error<T, E>> handler) {
            handler.accept(this);
        }

        @Override
        public <U> Result<U, E> map(Function<T, U> mapper) {
            return new Error<>(message);
        }

        @Override
        public <U> Result<U, E> flatMap(Function<T, Result<U, E>> mapper) {
            return new Error<>(message);
        }

        @Override
        public <U> Result<T, U> mapError(Function<E, U> mapper) {
            return new Error<>(mapper.apply(message));
        }
    }

    interface UnsafeOperation<T> {
        T run() throws Exception;
    }
}
