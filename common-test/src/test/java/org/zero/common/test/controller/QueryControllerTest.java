package org.zero.common.test.controller;

import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

import static java.lang.Boolean.TRUE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/6
 */
@WebMvcTest(controllers = QueryController.class)
class QueryControllerTest {
    @Resource
    MockMvc mockMvc;

    @Test
    void q1() throws Exception {
        String body = mockMvc.perform(get("/query/q1")
                        .queryParam("fields", "id, name")
                        // .queryParam("fields[0].name", "id")
                        // .queryParam("fields[1].name", "name")
                        // .queryParam("fields[1].alias", "username")
                        .queryParam("groupings", "a", "b", "c")
                        .queryParam("havings[0].field", "id")
                        .queryParam("havings[0].operator", "NULL_NE")
                        .queryParam("havings[0].value", "1")
                        .queryParam("collations[0].field", "id")
                        .queryParam("collations[1].field", "age")
                        .queryParam("collations[1].order", "DESC")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", IsEqual.equalTo(200), Integer.class))
                .andExpect(jsonPath("$.success").value(TRUE))
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);
        System.out.println(body);
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }
}