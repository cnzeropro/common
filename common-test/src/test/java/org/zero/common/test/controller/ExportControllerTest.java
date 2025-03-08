package org.zero.common.test.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/7
 */
// @WebMvcTest(ExportController.class)
// @ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@AutoConfigureMockMvc
@SpringBootTest
class ExportControllerTest {
    @Resource
    MockMvc mockMvc;

    /**
     * <a href="http://127.0.0.1:34567/export/e1">Test</a>
     */
    @Test
    void e1() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/export/e1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andReturn()
                .getResponse();
        String filename = Optional.ofNullable(response.getHeader(HttpHeaders.CONTENT_DISPOSITION))
                .map(ContentDisposition::parse)
                .map(ContentDisposition::getFilename)
                .orElse("unknown.data");
        byte[] body = response.getContentAsByteArray();
        Path path = Files.write(Paths.get(downloadDir.toString(), filename), body, StandardOpenOption.CREATE);
        System.out.println("File path: " + path);
    }

    /**
     * <a href="http://127.0.0.1:34567/export/e2">Test</a>
     */
    @Test
    void e2() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/export/e2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andReturn()
                .getResponse();
        String filename = Optional.ofNullable(response.getHeader(HttpHeaders.CONTENT_DISPOSITION))
                .map(ContentDisposition::parse)
                .map(ContentDisposition::getFilename)
                .orElse("unknown.data");
        byte[] body = response.getContentAsByteArray();
        Path path = Files.write(Paths.get(downloadDir.toString(), filename), body, StandardOpenOption.CREATE);
        System.out.println("File path: " + path);
    }

    /**
     * <a href="http://127.0.0.1:34567/export/e3">Test</a>
     */
    @Test
    void e3() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/export/e3"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/zip"))
                .andReturn()
                .getResponse();
        String filename = Optional.ofNullable(response.getHeader(HttpHeaders.CONTENT_DISPOSITION))
                .map(ContentDisposition::parse)
                .map(ContentDisposition::getFilename)
                .orElse("unknown.data");
        byte[] body = response.getContentAsByteArray();
        Path path = Files.write(Paths.get(downloadDir.toString(), filename), body, StandardOpenOption.CREATE);
        System.out.println("File path: " + path);
    }

    Path downloadDir = Paths.get("target", "download");

    @BeforeEach
    void setUp() throws IOException {
        if (!Files.exists(downloadDir)) {
            Files.createDirectories(downloadDir);
        }
    }

    // @AfterEach
    // void tearDown() throws IOException {
    //     Files.deleteIfExists(downloadDir);
    // }
}