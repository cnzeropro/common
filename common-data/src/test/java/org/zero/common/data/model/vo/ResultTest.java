package org.zero.common.data.model.vo;

import org.junit.jupiter.api.Test;
import org.zero.common.data.enumeration.Gender;
import org.zero.common.data.enumeration.Status;
import org.zero.common.data.enumeration.SysError;
import org.zero.common.data.model.StudentDTO;
import org.zero.common.data.model.dto.PageDTO;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/29
 */
class ResultTest {

    @Test
    void ok() {
        Result<Void> okVoid = Result.ok();
        System.out.println(okVoid);
        Result<Integer> okInt = Result.ok(10);
        System.out.println(okInt);
        Result<PageDTO<StudentDTO>> okPageStudent = Result.ok("分页查询成功", PageDTO.<StudentDTO>of()
                .setPageSize(20L)
                .setCurrentPage(12L)
                .setRecordCount(107L)
                .setRecords(Arrays.asList(StudentDTO.builder()
                                .id(1L)
                                .code("s00001")
                                .name("小明")
                                .gender(Gender.MALE)
                                .status(Status.FREEZE)
                                .createTime(LocalDateTime.now())
                                .updateBy("admin")
                                .build(),
                        StudentDTO.builder()
                                .id(2L)
                                .code("s00002")
                                .name("小红")
                                .gender(Gender.FEMALE)
                                .status(Status.NORMAL)
                                .createTime(LocalDateTime.now())
                                .updateBy("admin")
                                .build())));
        System.out.println(okPageStudent);
    }

    @Test
    void error() {
        Result<Void> error = Result.error("登录失败");
        System.out.println(error);
    }

    @Test
    void fail() {
        Result<Void> failVoid = Result.fail();
        System.out.println(failVoid);
        Result<Double> failDouble = Result.fail("fail");
        System.out.println(failDouble);
        Result<Void> fail404 = Result.fail(404, "资源未找到", SysError.ERROR);
        System.out.println(fail404);
    }
}