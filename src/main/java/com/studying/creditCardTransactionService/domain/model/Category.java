package com.studying.creditCardTransactionService.domain.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public enum Category {
    FOOD("5411", "5412"), MEAL("5811", "5812"), CASH;

    private static final Map<String, Category> mapMCC = new HashMap<>();
    private final Set<String> codes;

    static {
        Arrays
                .stream(Category.values())
                .forEach(category -> category.codes
                        .forEach(code -> mapMCC.put(code, category))
                        );
    }

    Category(final String ... mccs) {
        this.codes = new HashSet<>();
        if (Objects.nonNull(mccs)) {
            this.codes.addAll(Arrays.asList(mccs));
        }
    }

    public static Category fromMCC(final String mcc) {
        Objects.requireNonNull(mcc, "mcc must not be null");
        return mapMCC.getOrDefault(mcc, CASH);
    }
}
