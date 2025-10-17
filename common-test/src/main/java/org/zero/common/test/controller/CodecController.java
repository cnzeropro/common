package org.zero.common.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zero.common.core.support.api.cipher.encryption.Encryption;
import org.zero.common.core.support.api.cipher.encryption.EncryptionResponseBodyAdvice;
import org.zero.common.data.model.view.Result;
import org.zero.common.test.model.JobPO;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/6
 */
@Slf4j
@RestController
@RequestMapping("codec")
public class CodecController {
    /**
     * @see EncryptionResponseBodyAdvice
     */
    @Encryption
    @RequestMapping("c1")
    public Result<JobPO> c1(@RequestBody JobPO job) {
        return Result.ok(job);
    }
}
