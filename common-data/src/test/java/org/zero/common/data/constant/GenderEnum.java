package org.zero.common.data.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/12/26
 */
@Getter
@AllArgsConstructor
// 当成pojo序列化成json
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GenderEnum implements IEnum<Integer> {
    MALE(1, "男"),
    FEMALE(2, "女"),
    ;

    private final Integer code;
    private final String name;

    @Override
    public Integer getValue() {
        return code;
    }

    /**
     * jackson反序列化使用
     */
    @JsonCreator
    public static GenderEnum of(Integer code) {
        for (GenderEnum e : values()) {
            if (e.getValue().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
