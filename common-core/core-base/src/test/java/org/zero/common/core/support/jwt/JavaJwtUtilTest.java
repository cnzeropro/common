package org.zero.common.core.support.jwt;

import com.auth0.jwt.interfaces.Claim;
import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/8/15
 */
class JavaJwtUtilTest {

	@Test
	void sign() {
		String password = "123456";
		String token = JavaJwtUtil.sign(1860267503897313282L, password, Constant.ACCESS_EXPIRE_TIME);
		System.out.println(token);
	}

	@Test
	void verify() {
		String password = "123456";
		String token = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiJlY2FkYTVmYy0yNWEzLTQ3YjEtYjBhNi03OGEyNzJkZmFlZTMiLCJpc3MiOiJzeXN0ZW0iLCJzdWIiOiJhdXRoIiwidXNlcklkIjoiMSIsIm5iZiI6MTc1NTI0NjU2NywiaWF0IjoxNzU1MjQ2NTY3LCJleHAiOjE3NTUyNDgzNjd9.zmwViuWycB0lpBoSBFZ0elT6Gogpe8TOHul-iI82dWWR-NxUjspM9c2YApbXXcEOfprCX7zJnMt6oIqRQwtTDQ";
		boolean verify = JavaJwtUtil.verify(token, password);
		System.out.println(verify);
	}

	@Test
	void getUser() {
		String password = "123456";
		String token = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI5MzU1MDljYy1lYWNlLTQ3YWQtODEwNy1mM2I1NjhlY2I5ZWMiLCJpc3MiOiJzeXN0ZW0iLCJzdWIiOiJhdXRoIiwidXNlciI6MTg2MDI2NzUwMzg5NzMxMzI4MiwibmJmIjoxNzU2Nzc2MDUzLCJpYXQiOjE3NTY3NzYwNTMsImV4cCI6MTc1Njc3Nzg1M30.vqtR91NHagHqHrgBeCgTCJDvvTDvQypUuIK019l3sKZaaqiIvUQH2xjXoVMfNEnADqqFI3FyBMSoIKf5YmOoNw";
		Claim user = JavaJwtUtil.getUser(token, password);
		System.out.println(user.asLong());
	}
}