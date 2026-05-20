package org.zero.common.test.controller;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.zero.common.core.support.bean.dynamic.DynamicBean;
import org.zero.common.data.model.query.ListQO;
import org.zero.common.data.model.query.ReportQO;
import org.zero.common.data.model.view.Result;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/6
 */
class QueryControllerTest {
	private final QueryController controller = new QueryController();

	@Test
	void businessShouldEchoQueryObject() {
		QueryController.UserListQO query = new QueryController.UserListQO();
		query.setName("zero");
		query.setStatus("enabled");

		Result<QueryController.UserListQO> result = controller.business(query);

		assertTrue(result.isSuccess());
		assertSame(query, result.getData());
	}

	@Test
	void searchShouldEchoListQueryObject() {
		ListQO query = new ListQO();

		Result<ListQO> result = controller.search(query);

		assertTrue(result.isSuccess());
		assertSame(query, result.getData());
	}

	@Test
	void reportShouldEchoReportQueryObject() {
		ReportQO query = new ReportQO();

		Result<ReportQO> result = controller.report(query);

		assertTrue(result.isSuccess());
		assertSame(query, result.getData());
	}

	@Test
	void q2ShouldEchoDynamicBean() {
		DynamicBean bean = DynamicBean.create()
				.set("a", Arrays.asList("mmm", "nnn"))
				.set("b", 154)
				.set("c", new Integer[]{6536, 4564});

		Result<DynamicBean> result = controller.q2(bean);

		assertTrue(result.isSuccess());
		assertSame(bean, result.getData());
	}

	@Test
	void q3ShouldEchoHutoolJsonObject() {
		JSONObject param = new JSONObject()
				.set("c", Arrays.asList(6536, 4564));

		Result<JSONObject> result = controller.q3(param);

		assertTrue(result.isSuccess());
		assertSame(param, result.getData());
	}

	@Test
	void q4ShouldEchoJacksonObjectNode() {
		ObjectNode param = new ObjectMapper().createObjectNode();
		param.put("b", 154);
		param.putArray("c").add(6536).add(4564);

		Result<ObjectNode> result = controller.q4(param);

		assertTrue(result.isSuccess());
		assertSame(param, result.getData());
	}
}
