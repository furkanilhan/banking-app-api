package com.furkan.banking.specification;

import com.furkan.banking.model.Account;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AccountSpecificationBuilder {

    private final List<Specification<Account>> specs;

    public AccountSpecificationBuilder() {
        this.specs = new ArrayList<>();
        this.specs.add((root, query, builder) -> builder.isFalse(root.get("isDeleted")));
    }

    public void with(String key, String operation, Object value) {
        specs.add((root, query, builder) -> {
            if (operation.equalsIgnoreCase(":")) {
                return builder.equal(root.get(key), value);
            } else if (operation.equalsIgnoreCase("like")) {
                return builder.like(builder.lower(root.get(key)), "%" + value.toString().toLowerCase() + "%");
            }
            return null;
        });
    }

    public Specification<Account> build() {
        if (specs.isEmpty()) {
            return (root, query, builder) -> builder.conjunction();
        }

        Specification<Account> result = specs.get(0);
        for (int i = 1; i < specs.size(); i++) {
            result = Specification.where(result).and(specs.get(i));
        }
        return result;
    }
}
