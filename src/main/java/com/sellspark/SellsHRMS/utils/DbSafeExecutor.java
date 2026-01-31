package com.sellspark.SellsHRMS.utils;

import java.util.function.Supplier;

import org.springframework.dao.DataIntegrityViolationException;

import com.sellspark.SellsHRMS.exception.core.DbExceptionTranslator;

public final class DbSafeExecutor {

    private DbSafeExecutor() {
    }

    public static void run(Runnable action) {
        try {
            action.run();
        } catch (DataIntegrityViolationException ex) {
            throw DbExceptionTranslator.translate(ex);
        }
    }

    public static <T> T call(Supplier<T> action) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException ex) {
            throw DbExceptionTranslator.translate(ex);
        }
    }
}
