package com.aimsio.util;

import java.util.LinkedList;
import java.util.List;

public class WhereBuilder {
    private WhereBuilder() {}

    public static class Builder {
        private List<String> conditions = new LinkedList<>();

        public Builder add(String condition) {
            if (condition != null && !condition.isEmpty()) {
                conditions.add(condition);
            }
            return this;
        }

        public String build() {
            return !conditions.isEmpty() ? "where " + String.join(" and ", conditions) : "";
        }
    }
}
