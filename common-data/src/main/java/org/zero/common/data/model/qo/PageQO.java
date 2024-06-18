package org.zero.common.data.model.qo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.zero.common.data.model.dto.PageDTO;

import javax.validation.constraints.Positive;

/**
 * 前端分页列表查询对象，两种使用方式：
 * 1、直接使用：直接用于承接前端传入参数（请求体 JSON 参数）
 * 2、继承使用：查询实体继承其并进行扩展（URL 参数）
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2021/1/5
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageQO extends BaseQO {
    /**
     * 页码
     */
    @Positive(message = "当前页码不能小于或等于0")
    private long pageNum = 1L;

    /**
     * 每页显示数
     */
    @Positive(message = "每页数目不能小于或等于0")
    private long pageSize = PageDTO.DEFAULT_PAGE_SIZE;
}
