package jp.zodiac.javaresult;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Result is a type that represents either success (Ok) or failure (Err).
 */
public sealed interface Result<T> {
    /**
     * Ok is a type of Result that represents success and contains a value.
     */
    public record Ok<T>(T value) implements Result<T> {
    }

    /**
     * Err is a type of Result that represents failure and contains an exception.
     */
    public record Err<T>(Exception exception) implements Result<T> {
    }

    /**
     * Returns true if the result is Ok.
     *
     * @return true if the result is Ok, false otherwise.
     */
    default boolean isOk() {
        return this instanceof Ok;
    }

    /**
     * Returns true if the result is Ok and the value satisfies the predicate.
     *
     * @param predicate The predicate to apply to the contained Ok value.
     * @return true if the result is Ok and the value satisfies the predicate, false
     *         otherwise.
     */
    default public boolean isOkAnd(Predicate<T> predicate) {
        return this instanceof Ok && predicate.test(((Ok<T>) this).value);
    }

    /**
     * Returns true if the result is Err.
     *
     * @return true if the result is Err, false otherwise.
     */
    default boolean isErr() {
        return this instanceof Err;
    }

    /**
     * Returns true if the result is Err and the exception satisfies the predicate.
     *
     * @param predicate The predicate to apply to the contained Err value.
     * @return true if the result is Err and the exception satisfies the predicate,
     *         false otherwise.
     */
    default boolean isErrAnd(Predicate<Exception> predicate) {
        return this instanceof Err && predicate.test(((Err<T>) this).exception);
    }

    /**
     * Returns the value if the result is Ok.
     *
     * @return an Optional containing the Ok value if the result is Ok, an empty
     *         Optional otherwise.
     */
    default Optional<T> ok() {
        return this instanceof Ok ? Optional.of(((Ok<T>) this).value) : Optional.empty();
    }

    /**
     * Returns the exception if the result is Err.
     *
     * @return an Optional containing the Err value if the result is Err, an empty
     *         Optional otherwise.
     */
    default Optional<Exception> err() {
        return this instanceof Err ? Optional.of(((Err<T>) this).exception) : Optional.empty();
    }

    /**
     * Applies the provided function to the contained Ok value and returns a new
     * Result.
     *
     * @param mapper The function to apply to the contained Ok value.
     * @return A new Result obtained by applying the provided function to the
     *         contained Ok value.
     */
    default <U> Result<U> map(Function<T, U> mapper) {
        return this instanceof Ok ? new Ok<>(mapper.apply(((Ok<T>) this).value)) : new Err<>(((Err<T>) this).exception);
    }

    /**
     * Applies the provided function to the contained Ok value and returns the
     * result, or returns the provided default.
     *
     * @param defaultValue The default value to return if this Result is Err.
     * @param mapper       The function to apply to the contained Ok value.
     * @return The value obtained by applying the provided function to the contained
     *         Ok value, or the provided default.
     */
    default public <U> U mapOr(U defaultValue, Function<T, U> mapper) {
        return this instanceof Ok ? (mapper.apply(((Ok<T>) this).value)) : defaultValue;
    }

    /**
     * Applies the provided function to the contained Ok value and returns the
     * result, or applies the provided default function to the contained Err value.
     *
     * @param okMapper  The function to apply to the contained Ok value.
     * @param errMapper The function to apply to the contained Err value.
     * @return The value obtained by applying the provided function to the contained
     *         Ok value, or the value obtained by applying the provided default
     *         function to the contained Err value.
     */
    default public <U> U mapOrElse(Function<T, U> okMapper, Function<Exception, U> errMapper) {
        return this instanceof Ok ? (okMapper.apply(((Ok<T>) this).value)) : errMapper.apply(((Err<T>) this).exception);
    }

    /**
     * Applies the provided function to the contained Err value and returns a new
     * Result.
     *
     * @param mapper The function to apply to the contained Err value.
     * @return A new Result obtained by applying the provided function to the
     *         contained Err value if the result is Err, otherwise returns the
     *         original Result.
     */
    default public <E extends Exception> Result<T> mapErr(Function<Exception, E> mapper) {
        return this instanceof Ok ? this : new Err<>((Exception) mapper.apply(((Err<T>) this).exception));
    }

    /**
     * Returns the contained Ok value, or throws an exception with the provided
     * message.
     *
     * @param message The message to use for the exception if the result is Err.
     * @return The contained Ok value.
     * @throws RuntimeException if the result is Err.
     */
    default public T expect(String message) {
        if (this instanceof Ok) {
            return ((Ok<T>) this).value;
        }
        throw new RuntimeException(message, ((Err<T>) this).exception);
    }

    /**
     * Returns the contained Ok value.
     *
     * @return The contained Ok value.
     * @throws RuntimeException if the result is Err.
     */
    default public T unwrap() {
        if (this instanceof Ok) {
            return ((Ok<T>) this).value;
        }
        throw new RuntimeException(((Err<T>) this).exception);
    }

    /**
     * Returns the contained Err value, or throws an exception with the provided
     * message.
     *
     * @param message The message to use for the exception if the result is Ok.
     * @return The contained Err value.
     * @throws RuntimeException if the result is Ok.
     */
    default public Exception expectErr(String message) {
        if (this instanceof Err) {
            return ((Err<T>) this).exception;
        }
        throw new RuntimeException(message);
    }

    /**
     * Returns the contained Err value.
     *
     * @return The contained Err value.
     * @throws RuntimeException if the result is Ok.
     */
    default public Exception unwrapErr() {
        if (this instanceof Err) {
            return ((Err<T>) this).exception;
        }
        throw new RuntimeException("No Err value present");
    }

    /**
     * Returns the contained Ok value or a provided default.
     *
     * @param defaultResult The default Result<U> to return if this Result is Err.
     * @return The contained Ok value or the provided default.
     */
    default public <U> Result<U> and(Result<U> defaultResult) {
        return this instanceof Ok ? defaultResult : new Err<>(((Err<T>) this).exception);
    }

    /**
     * Applies the provided function to the contained Ok value, or returns the
     * contained Err value untouched.
     *
     * @param mapper The function to apply to the contained Ok value.
     * @return The Result<U> obtained by applying the provided function to the
     *         contained Ok value, or the contained Err value.
     */
    default public <U> Result<U> andThen(Function<T, Result<U>> mapper) {
        return this instanceof Ok ? mapper.apply(((Ok<T>) this).value) : new Err<>(((Err<T>) this).exception);
    }

    /**
     * Returns the contained Ok value, or if the result is Err, returns the provided
     * default.
     *
     * @param defaultResult The default Result<T> to return if this Result is Err.
     * @return The contained Ok value or the provided default.
     */
    default public Result<T> or(Result<T> defaultResult) {
        return this instanceof Ok ? this : defaultResult;
    }

    /**
     * Returns the contained Ok value, or if the result is Err, applies the provided
     * function to the contained Err value.
     *
     * @param mapper The function to apply to the contained Err value.
     * @return The Result<T> obtained by applying the provided function to the
     *         contained Err value, or the contained Ok value.
     */
    default public Result<T> orElse(Function<Exception, Result<T>> mapper) {
        return this instanceof Ok ? this : mapper.apply(((Err<T>) this).exception);
    }

    /**
     * Returns the contained Ok value, or if the result is Err, returns the provided
     * default.
     *
     * @param defaultValue The default value to return if this Result is Err.
     * @return The contained Ok value or the provided default.
     */
    default public T unwrapOr(T defaultValue) {
        return this instanceof Ok ? ((Ok<T>) this).value : defaultValue;
    }

    /**
     * Returns the contained Ok value, or if the result is Err, applies the provided
     * function to the contained Err value.
     *
     * @param mapper The function to apply to the contained Err value.
     * @return The value obtained by applying the provided function to the contained
     *         Err value, or the contained Ok value.
     */
    default public T unwrapOrElse(Function<Exception, T> mapper) {
        return this instanceof Ok ? ((Ok<T>) this).value : mapper.apply(((Err<T>) this).exception);
    }
}
