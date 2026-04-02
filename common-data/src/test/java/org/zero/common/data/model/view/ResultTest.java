package org.zero.common.data.model.view;

import org.junit.jupiter.api.Test;
import org.zero.common.data.enumeration.Gender;
import org.zero.common.data.enumeration.UserStatus;
import org.zero.common.data.model.transfer.UserDTO;

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
		Result<SmartPageVO<UserDTO>> okPageStudent = Result.ok("鍒嗛〉鏌ヨ鎴愬姛", SmartPageVO.<UserDTO>of()
			.setSize(20L)
			.setCurrent(12L)
			.setCount(107L)
			.setContents(Arrays.asList(UserDTO.builder()
					.id(1566546546L)
					.code("s00001")
					.name("灏忔槑")
					.gender(Gender.MALE)
					.status(UserStatus.FREEZE)
					.createdAt(LocalDateTime.now())
					.updatedBy(1L)
					.build(),
				UserDTO.builder()
					.id(2344353L)
					.code("s00002")
					.name("灏忕孩")
					.gender(Gender.FEMALE)
					.status(UserStatus.NORMAL)
					.createdAt(LocalDateTime.now())
					.updatedBy(1L)
					.build())));
		System.out.println(okPageStudent);
	}

	@Test
	void error() {
		Result<Void> error = Result.error("鐧诲綍澶辫触");
		System.out.println(error);
	}

	@Test
	void fail() {
		Result<Void> failVoid = Result.error();
		System.out.println(failVoid);
		Result<Double> failDouble = Result.error("fail");
		System.out.println(failDouble);
		Result<Void> fail404 = Result.error(404, "璧勬簮鏈壘鍒?");
		System.out.println(fail404);
	}
}
