package org.zero.common.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Zero
 * @since 2023/4/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestBean {
    private byte b;
    private short s;
    private int i;
    private long l;
    private float f;
    private double d;
    private boolean bool;

    private String str;
    private BigDecimal bigDecimal;
    private BigInteger bigInteger;
    private Date date;
    private Calendar calendar;
    private LocalDateTime localDateTime;
    private LocalDate localDate;
    private LocalTime localTime;

    private Map<String, Object> map;
    private List<Object> list;
    private Set<Object> set;
}
