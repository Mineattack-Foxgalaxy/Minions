package io.github.skippyall.minions.util;

import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FutureUtil {
    public static <T> CompletableFuture<T> failIfNull(@Nullable T v) {
        if(v != null) {
            //noinspection RedundantTypeArguments
            return CompletableFuture.<T>completedFuture(v);
        } else {
            return CompletableFuture.failedFuture(new NullPointerException());
        }
    }
}
