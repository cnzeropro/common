package org.zero.common.test.controller;

import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
// @WebMvcTest(QueryController.class)
// @ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@AutoConfigureMockMvc
@SpringBootTest
class QueryControllerTest {
    @Resource
    MockMvc mockMvc;

    /**
     * <a href="http://127.0.0.1:34567/query/q1?fields=id,name&groupings=a,b,c&havings[0].field=id&havings[0].operator=NULL_NE&havings[0].value=1&collations[0].field=id&collations[1].field=age&collations[1].order=DESC">Test</a>
     */
    @Test
    void q1() throws Exception {
        String body = mockMvc.perform(get("/query/q1")
                        // .queryParam("fields", "id,name")
                        .queryParam("fields", "id", "name")
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

    /**
     * <a href="http://127.0.0.1:34567/query/q2?a=mmm,nnn&b=154&c=6536&c=4564">Test</a>
     */
    @Test
    void q2() throws Exception {
        String body = mockMvc.perform(get("/query/q2")
                        .queryParam("a", "mmm,nnn")
                        // .queryParam("a", "mmm", "nnn")
                        .queryParam("b", "154")
                        .queryParam("c", "6536")
                        .queryParam("c", "4564"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", IsEqual.equalTo(200), Integer.class))
                .andExpect(jsonPath("$.success").value(TRUE))
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);
        System.out.println(body);
    }

    /**
     * <a href="http://127.0.0.1:34567/query/q3?a=mmm,nnn&b=154&c=6536&c=4564">Test</a>
     */
    @Test
    void q3() throws Exception {
        String body = mockMvc.perform(get("/query/q3")
                        .queryParam("a", "mmm", "nnn")
                        .queryParam("b", "154")
                        .queryParam("c", "6536")
                        .queryParam("c", "4564"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", IsEqual.equalTo(200), Integer.class))
                .andExpect(jsonPath("$.success").value(TRUE))
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);
        System.out.println(body);
    }

    /**
     * <a href="http://127.0.0.1:34567/query/q4?a=mmm,nnn&b=154&c=6536&c=4564">Test</a>
     */
    @Test
    void q4() throws Exception {
        String body = mockMvc.perform(get("/query/q3")
                        .queryParam("a", "mmm", "nnn")
                        .queryParam("b", "154")
                        .queryParam("c", "6536")
                        .queryParam("c", "4564"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", IsEqual.equalTo(200), Integer.class))
                .andExpect(jsonPath("$.success").value(TRUE))
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);
        System.out.println(body);
    }
}