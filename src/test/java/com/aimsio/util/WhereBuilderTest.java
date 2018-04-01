package com.aimsio.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WhereBuilderTest {

    @Test
    public void noConditionsOk() {
        String where = new WhereBuilder.Builder().build();
        assertEquals("", where);
    }

    @Test
    public void oneConditionOk() {
        String where = new WhereBuilder.Builder()
                            .add("1=1")
                            .build();
        assertEquals("where 1=1", where);
    }

    @Test
    public void twoConditionsOk() {
        String where = new WhereBuilder.Builder()
                            .add("1=1")
                            .add("2=2")
                            .build();
        assertEquals("where 1=1 and 2=2", where);
    }

    @Test
    public void emptyConditionOk() {
        String where = new WhereBuilder.Builder()
                .add("")
                .build();
        assertEquals("", where);
    }

    @Test
    public void twoConditionsOneEmptyOk() {
        String where = new WhereBuilder.Builder()
                .add("")
                .add("2=2")
                .build();
        assertEquals("where 2=2", where);
    }

}