package org.zero.common.test.controller;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.util.Objects.requireNonNull;
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

    @Test
    void e1() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/export/e1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM))
                .andReturn()
                .getResponse();
        String contentDisposition = requireNonNull(response.getHeader(HttpHeaders.CONTENT_DISPOSITION), "Content-Disposition header is null");
        String filename = ContentDisposition.parse(contentDisposition).getFilename();
        byte[] body = response.getContentAsByteArray();
        Path downloadDir = Paths.get("target", "download");
        if (!Files.exists(downloadDir)) {
            Files.createDirectories(downloadDir);
        }
        Path path = Files.write(Paths.get(downloadDir.toString(), filename), body, StandardOpenOption.CREATE);
        System.out.println("file path: " + path);
    }

    @Test
    void e2() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/export/e2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")))
                .andReturn()
                .getResponse();
        String contentDisposition = requireNonNull(response.getHeader(HttpHeaders.CONTENT_DISPOSITION), "Content-Disposition header is null");
        String filename = ContentDisposition.parse(contentDisposition).getFilename();
        byte[] body = response.getContentAsByteArray();
        Path downloadDir = Paths.get("target", "download");
        if (!Files.exists(downloadDir)) {
            Files.createDirectories(downloadDir);
        }
        Path path = Files.write(Paths.get(downloadDir.toString(), filename), body, StandardOpenOption.CREATE);
        System.out.println("file path: " + path);
    }

    @Test
    void e3() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/export/e3"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM))
                .andReturn()
                .getResponse();
        String contentDisposition = requireNonNull(response.getHeader(HttpHeaders.CONTENT_DISPOSITION), "Content-Disposition header is null");
        String filename = ContentDisposition.parse(contentDisposition).getFilename();
        byte[] body = response.getContentAsByteArray();
        Path downloadDir = Paths.get("target", "download");
        if (!Files.exists(downloadDir)) {
            Files.createDirectories(downloadDir);
        }
        Path path = Files.write(Paths.get(downloadDir.toString(), filename), body, StandardOpenOption.CREATE);
        System.out.println("file path: " + path);
    }
}