package org.zero.common.data.model.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.zero.common.data.constant.CommonConstant;

import java.io.Serializable;

/**
 * 前端分页列表查询参数对象，两种使用方式：
 * <ul>
 *     <li>直接使用：直接用于承接前端传入参数</li>
 *     <li>继承使用：查询实体继承其并进行扩展</li>
 * </ul>
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2021/1/5
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageQO implements Serializable {
    /**
     * 页码
     */
	@Builder.Default
    private long pageNum = 1L;

    /**
     * 每页显示数
     */
	@Builder.Default
    private long pageSize = CommonConstant.DEFAULT_PAGE_SIZE;
}
