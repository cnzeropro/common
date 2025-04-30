package org.zero.common.core.util.hutool.core.text.csv;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/17
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CsvReadConfig extends cn.hutool.core.text.csv.CsvReadConfig {
    protected Charset charset = StandardCharsets.UTF_8;

    public static CsvReadConfig defaultConfig() {
        return new CsvReadConfig();
    }
}
