package com.example.vhomestay.enums;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum RefundStatus {
    PENDING("Đang chờ hoàn tiền"),
    REFUNDED("Đã hoàn tiền"),
    NOT_REFUNDED("Không hỗ trợ hoàn tiền do huỷ sau thời gian quy định");

    private String description;

}
