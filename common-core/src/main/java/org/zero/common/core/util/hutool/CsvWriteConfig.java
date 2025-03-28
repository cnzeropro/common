package org.zero.common.core.util.hutool;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/13
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CsvWriteConfig extends cn.hutool.core.text.csv.CsvWriteConfig {
    protected boolean append;
    protected Charset charset = StandardCharsets.UTF_8;

    public static CsvWriteConfig defaultConfig() {
        return new CsvWriteConfig();
    }
}
