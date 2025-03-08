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
 * @since 2025/3/4
 */
// @WebMvcTest(ExportController.class)
// @ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@AutoConfigureMockMvc
@SpringBootTest
class JacksonControllerTest {
    @Resource
    MockMvc mockMvc;

    /**
     * <a href="http://127.0.0.1:34567/jackson/null">Test</a>
     */
    @Test
    void jsonNullType() throws Exception {
        String body = mockMvc.perform(get("/jackson/null")
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
     * <a href="http://127.0.0.1:34567/jackson/datetime">Test</a>
     */
    @Test
    void datetimeType() throws Exception {
        String body = mockMvc.perform(get("/jackson/datetime")
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
     * <a href="http://127.0.0.1:34567/jackson/long">Test</a>
     */
    @Test
    void longType() throws Exception {
        String body = mockMvc.perform(get("/jackson/long")
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
     * <a href="http://127.0.0.1:34567/jackson/double">Test</a>
     */
    @Test
    void doubleType() throws Exception {
        String body = mockMvc.perform(get("/jackson/double")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", IsEqual.equalTo(200), Integer.class))
                .andExpect(jsonPath("$.success").value(TRUE))
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);
        System.out.println(body);
    }
}