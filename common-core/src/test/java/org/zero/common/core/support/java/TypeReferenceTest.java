package org.zero.common.core.support.java;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yufa.wang (yufa.wang@ronganchina.com)
 * @since 2023/7/13
 */
class TypeReferenceTest {
    @Test
    void test() {
        TypeReference<List<Map<String, Set<BigDecimal>>>> reference = new TypeReference<List<Map<String, Set<BigDecimal>>>>() {
        };
        com.fasterxml.jackson.core.type.TypeReference<Map<String, Integer>> reference1 = new com.fasterxml.jackson.core.type.TypeReference<Map<String, Integer>>() {
        };
        ParameterizedTypeReference<Map<String, Map<Integer, Object>>> reference2 = new ParameterizedTypeReference<Map<String, Map<Integer, Object>>>() {
        };

        get(reference);
        System.out.println("===========================================================");
        get(reference1);
        System.out.println("===========================================================");
        get(reference2);
    }

    private <T> void get(TypeReference<T> reference) {
        Type type = reference.getType();
        System.out.println(type);
    }

    public <T> void get(com.fasterxml.jackson.core.type.TypeReference<T> reference) {
        Type type = reference.getType();
        System.out.println(type);

        ResolvableType resolvableType = ResolvableType.forType(type);
        Class<T> rawClass = (Class<T>) resolvableType.getRawClass();
        System.out.println(rawClass);
        ResolvableType[] generics = resolvableType.getGenerics();
        System.out.println(Arrays.toString(generics));
        ResolvableType rv = ResolvableType.forClassWithGenerics(rawClass, generics);
        System.out.println(rv);
    }

    public <T> void get(ParameterizedTypeReference<T> reference) {
        Type type = reference.getType();
        System.out.println(type);
    }
}