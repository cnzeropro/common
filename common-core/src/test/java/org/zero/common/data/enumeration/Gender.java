package org.zero.common.data.enumeration;

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
public enum Gender implements IEnum<Integer> {
    MALE(1, "男"),
    FEMALE(2, "女"),
    ;

    private final Integer type;
    private final String name;

    /**
     * MP 反序列化使用
     */
    @Override
    public Integer getValue() {
        return type;
    }

    /**
     * jackson反序列化使用
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Gender of(Integer type) {
        for (Gender gender : values()) {
            if (gender.type.equals(type)) {
                return gender;
            }
        }
        return null;
    }
}
