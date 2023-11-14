package jp.zodiac.javaresult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jp.zodiac.javaresult.Result.Err;
import jp.zodiac.javaresult.Result.Ok;

public class ResultTest {
    @Test
    public void testIsOk() {
        Ok<Integer> ok = new Ok<>(2);
        assertTrue(ok.isOk());
        Err<Integer> err = new Err<>(new Exception());
        assertFalse(err.isOk());
    }

    @Test
    public void testIsOkAnd() {
        Ok<Integer> ok1 = new Ok<>(2);
        assertTrue(ok1.isOkAnd(v -> v > 1));
        Ok<Integer> ok2 = new Ok<>(0);
        assertFalse(ok2.isOkAnd(v -> v > 1));
        Err<Integer> err = new Err<>(new Exception());
        assertFalse(err.isOkAnd(v -> v > 1));
    }

    @Test
    public void testIsErr() {
        Ok<Integer> ok = new Ok<>(2);
        assertFalse(ok.isErr());
        Err<Integer> err = new Err<>(new Exception());
        assertTrue(err.isErr());
    }

    @Test
    public void testIsErrAnd() {
        Ok<Integer> ok = new Ok<Integer>(2);
        assertFalse(ok.isErrAnd(e -> e.getClass() == Exception.class));
        Err<Integer> err = new Err<>(new Exception());
        assertTrue(err.isErrAnd(e -> e.getClass() == Exception.class));

    }

    @Test
    public void testOk() {
        Ok<Integer> ok = new Ok<>(2);
        assertEquals(2, ok.ok().get());
        Err<Integer> err = new Err<>(new Exception());
        assertTrue(err.ok().isEmpty());
    }

    @Test
    public void testErr() {
        Ok<Integer> ok = new Ok<>(2);
        assertTrue(ok.err().isEmpty());
        Err<Integer> err = new Err<>(new Exception());
        assertEquals(Exception.class, err.err().get().getClass());
    }

    @Test
    public void testMap() {
        Ok<Integer> ok = new Ok<>(2);
        assertEquals(new Ok<>(4), ok.map(v -> v * 2));
        Err<Integer> err = new Err<>(new Exception());
        assertEquals(Exception.class, err.map(v -> v * 2).err().get().getClass());
    }

    @Test
    public void testMapWithException() {
        Ok<Integer> ok = new Ok<>(2);
        assertThrows(RuntimeException.class, () -> ok.map(v -> {
            throw new RuntimeException();
        }));
    }

    @Test
    public void testMapOr() {
        Ok<String> ok = new Ok<>("foo");
        assertEquals(3, ok.mapOr(42, v -> v.length()));
        Err<String> err = new Err<>(new Exception("foo"));
        assertEquals(42, err.mapOr(42, v -> v.length()));
    }

    @Test
    public void testMapOrWithException() {
        Ok<String> ok = new Ok<>("foo");
        assertThrows(RuntimeException.class, () -> ok.mapOr(42, v -> {
            throw new RuntimeException();
        }));
    }

    @Test
    public void testMapOrElse() {
        Ok<String> ok = new Ok<>("foo");
        assertEquals(3, ok.<Integer>mapOrElse(v -> v.length(), e -> e.getMessage().length()));
        Err<String> err = new Err<>(new Exception("foo"));
        assertEquals(3, err.<Integer>mapOrElse(v -> v.length(), e -> e.getMessage().length()));
    }

    @Test
    public void testMapOrElseWithException() {
        Ok<String> ok = new Ok<>("foo");
        assertThrows(RuntimeException.class, () -> ok.mapOrElse(v -> {
            throw new RuntimeException();
        }, e -> e.getMessage().length()));
        Err<String> err = new Err<>(new Exception("foo"));
        assertThrows(RuntimeException.class, () -> err.mapOrElse(v -> v.length(), e -> {
            throw new RuntimeException();
        }));
    }

    @Test
    public void testMapErr() {
        Ok<Integer> ok = new Ok<>(2);
        assertEquals(new Ok<>(2), ok.mapErr(e -> new RuntimeException()));
        Err<Integer> err = new Err<>(new Exception());
        assertEquals(RuntimeException.class, err.mapErr(e -> new RuntimeException()).err().get().getClass());
    }

    @Test
    public void testMapErrWithException() {
        Err<Integer> err = new Err<>(new Exception());
        assertThrows(RuntimeException.class, () -> err.mapErr(e -> {
            throw new RuntimeException();
        }));
    }

    @Test
    public void testExpect() {
        Result<String> ok = new Ok<>("foo");
        assertEquals("foo", ok.expect("bar"));
        Result<String> err = new Err<>(new Exception());
        RuntimeException e = assertThrows(RuntimeException.class, () -> err.expect("bar"));
        assertEquals("bar", e.getMessage());
    }

    @Test
    public void testUnwrap() {
        Result<Integer> ok = new Ok<>(2);
        assertEquals(2, ok.unwrap());
        Result<Integer> err = new Err<>(new Exception());
        RuntimeException e = assertThrows(RuntimeException.class, err::unwrap);
        assertEquals(Exception.class, e.getCause().getClass());
    }

    @Test
    public void testExpectErr() {
        Result<String> ok = new Ok<>("foo");
        RuntimeException e = assertThrows(RuntimeException.class, () -> ok.expectErr("bar"));
        assertEquals("bar", e.getMessage());
        Result<String> err = new Err<>(new Exception());
        assertEquals(Exception.class, err.expectErr("bar").getClass());
    }

    @Test
    public void testUnwrapErr() {
        Result<Integer> ok = new Ok<>(2);
        assertThrows(RuntimeException.class, ok::unwrapErr);
        Result<Integer> err = new Err<>(new Exception());
        assertEquals(Exception.class, err.unwrapErr().getClass());
    }

    @Test
    public void testAnd() {
        Result<Integer> ok = new Ok<>(2);
        assertEquals(new Ok<>("foo"), ((Ok<String>) ok.and(new Ok<>("foo"))));
        Result<Integer> err = new Err<>(new Exception());
        assertEquals(err, (Err<String>) err.and(new Ok<>("foo")));
    }

    @Test
    public void testAndThen() {
        Result<Integer> ok = new Ok<>(2);
        assertEquals(new Ok<String>("2"), ok.andThen(v -> new Ok<>(v.toString())));
        Result<Integer> err = new Err<>(new Exception());
        assertEquals(err, (Err<String>) err.andThen(v -> new Ok<>(v.toString())));
    }

    @Test
    public void testAndThenWithException() {
        Result<Integer> ok = new Ok<>(2);
        assertThrows(RuntimeException.class, () -> ok.andThen(v -> {
            throw new RuntimeException();
        }));
    }

    @Test
    public void testOr() {
        Result<Integer> ok = new Ok<>(2);
        assertEquals(ok, ok.or(new Ok<>(4)));
        Result<Integer> err = new Err<>(new Exception());
        assertEquals(new Ok<>(4), err.or(new Ok<>(4)));
    }

    @Test
    public void testOrElse() {
        Result<Integer> ok = new Ok<>(2);
        assertEquals(ok, ok.orElse(e -> new Ok<>(4)));
        Result<Integer> err = new Err<>(new Exception());
        assertEquals(new Ok<>(4), err.orElse(e -> new Ok<>(4)));
    }

    @Test
    public void testOrElseWithException() {
        Result<Integer> err = new Err<>(new Exception());
        assertThrows(RuntimeException.class, () -> err.orElse(e -> {
            throw new RuntimeException();
        }));
    }

    @Test
    public void testUnwrapOr() {
        Result<Integer> ok = new Ok<>(2);
        assertEquals(2, ok.unwrapOr(4));
        Result<Integer> err = new Err<>(new Exception());
        assertEquals(4, err.unwrapOr(4));
    }

    @Test
    public void testUnwrapOrElse() {
        Result<Integer> ok = new Ok<>(2);
        assertEquals(2, ok.unwrapOrElse(e -> 4));
        Result<Integer> err = new Err<>(new Exception());
        assertEquals(4, err.unwrapOrElse(e -> 4));
    }

    @Test
    public void testUnwrapOrElseWithException() {
        Result<Integer> err = new Err<>(new Exception());
        assertThrows(RuntimeException.class, () -> err.unwrapOrElse(e -> {
            throw new RuntimeException();
        }));
    }
}
