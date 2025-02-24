package org.zero.common.data.model.persistant;

import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/14
 */
class BasePOTest {
    @Test
    void test() {
        ProductPO product = ProductPO.builder()
                .id(1L)
                .name("apple")
                .description("Big Apple")
                .price(new BigDecimal("2.31"))
                .inventory(100L)
                .createdBy(65474L)
                .createdAt(LocalDateTime.now())
                .updatedBy(91232234L)
                .updatedAt(LocalDateTime.now())
                .deleted(Boolean.FALSE)
                .version(2L)
                .build();
        String jsonStr = JSONUtil.toJsonStr(product);
        System.out.println(jsonStr);
    }
}