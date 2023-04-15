package com.usktea.plainold.dtos;

import com.usktea.plainold.models.option.Size;

public class CostDto {
    private Long amount;

    public CostDto() {
    }

    public CostDto(Long amount) {
        this.amount = amount;
    }

    public static CostDto fake() {
        return new CostDto(12_500L);
    }

    public Long getAmount() {
        return amount;
    }
}
