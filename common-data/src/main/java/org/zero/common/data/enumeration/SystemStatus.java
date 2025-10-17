package org.zero.common.data.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 系统错误枚举
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/12/1
 */
@Getter
@RequiredArgsConstructor
public enum SystemStatus implements org.zero.common.data.exception.Status {
	/**
	 * 一切可行
	 */
	OK("00000", "ok"),
	/**
	 * 用户端错误
	 */
	CLIENT_ERROR("A0001", "client error"),
	/**
	 * 用户注册错误
	 */
	USER_REGISTER_ERROR("A0002", "user register error"),
	/**
	 * 用户未同意隐私协议
	 */
	PRIVACY_POLICY_NOT_AGREED("A0101", "privacy policy not agreed"),
	/**
	 * 注册国家或地区受限
	 */
	REGISTER_COUNTRY_OR_REGION_RESTRICTED("A0102", "register country or region restricted"),
	/**
	 * 用户名校验失败
	 */
	USERNAME_CHECK_FAILED("A0110", "username check failed"),
	/**
	 * 用户名已存在
	 */
	USERNAME_EXISTS("A0111", "username exists"),
	/**
	 * 用户名包含敏感词
	 */
	USERNAME_CONTAINS_SENSITIVE_WORD("A0112", "username contains sensitive word"),
	/**
	 * 用户名包含特殊字符
	 */
	USERNAME_CONTAINS_SPECIAL_CHARACTER("A0113", "username contains special character"),
	/**
	 * 密码校验失败
	 */
	PASSWORD_CHECK_FAILED("A0120", "password check failed"),
	/**
	 * 密码长度不够
	 */
	PASSWORD_LENGTH_NOT_ENOUGH("A0121", "password length not enough"),
	/**
	 * 密码强度不够
	 */
	PASSWORD_STRENGTH_NOT_ENOUGH("A0122", "password strength not enough"),
	/**
	 * 校验码输入错误
	 */
	VERIFICATION_CODE_INPUT_ERROR("A0130", "verification code input error"),
	/**
	 * 短信校验码输入错误
	 */
	SMS_VERIFICATION_CODE_INPUT_ERROR("A0131", "sms verification code input error"),
	/**
	 * 邮件校验码输入错误
	 */
	EMAIL_VERIFICATION_CODE_INPUT_ERROR("A0132", "email verification code input error"),
	/**
	 * 语音校验码输入错误
	 */
	VOICE_VERIFICATION_CODE_INPUT_ERROR("A0133", "voice verification code input error"),
	/**
	 * 用户证件异常
	 */
	INVALID_USER_IDENTITY("A0140", "invalid user identity"),
	/**
	 * 用户证件类型未选择
	 */
	USER_IDENTITY_TYPE_NOT_SELECTED("A0141", "user identity type not selected"),
	/**
	 * 大陆身份证编号校验非法
	 */
	CHINA_IDENTITY_NUMBER_CHECK_ILLEGAL("A0142", "china identity number check illegal"),
	/**
	 * 护照编号校验非法
	 */
	PASSPORT_NUMBER_CHECK_ILLEGAL("A0143", "passport number check illegal"),
	/**
	 * 军官证编号校验非法
	 */
	MILITARY_IDENTITY_NUMBER_CHECK_ILLEGAL("A0144", "military identity number check illegal"),
	/**
	 * 用户基本信息校验失败
	 */
	USER_BASIC_INFO_CHECK_FAILED("A0150", "user basic info check failed"),
	/**
	 * 手机格式校验失败
	 */
	MOBILE_FORMAT_CHECK_FAILED("A0151", "mobile format check failed"),
	/**
	 * 地址格式校验失败
	 */
	ADDRESS_FORMAT_CHECK_FAILED("A0152", "address format check failed"),
	/**
	 * 邮箱格式校验失败
	 */
	EMAIL_FORMAT_CHECK_FAILED("A0153", "email format check failed"),
	/**
	 * 用户登录异常
	 */
	USER_LOGIN_ERROR("A0200", "user login error"),
	/**
	 * 用户账户不存在
	 */
	USER_ACCOUNT_NOT_EXISTS("A0201", "user account not exists"),
	/**
	 * 用户账户被冻结
	 */
	USER_ACCOUNT_FROZEN("A0202", "user account frozen"),
	/**
	 * 用户账户已作废
	 */
	USER_ACCOUNT_INVALID("A0203", "user account invalid"),
	/**
	 * 用户密码错误
	 */
	USER_PASSWORD_ERROR("A0210", "user password error"),
	/**
	 * 用户输入密码错误次数超限
	 */
	USER_PASSWORD_ERROR_COUNT_OVER_LIMIT("A0211", "user password error count over limit"),
	/**
	 * 用户身份校验失败
	 */
	USER_IDENTITY_CHECK_FAILED("A0220", "user identity check failed"),
	/**
	 * 用户指纹识别失败
	 */
	USER_FINGERPRINT_IDENTIFICATION_FAILED("A0221", "user fingerprint identification failed"),
	/**
	 * 用户面容识别失败
	 */
	USER_FACE_IDENTIFICATION_FAILED("A0222", "user face identification failed"),
	/**
	 * 用户未获得第三方登录授权
	 */
	USER_THIRD_PARTY_LOGIN_NOT_AUTHORIZED("A0223", "user third party login not authorized"),
	/**
	 * 用户登录已过期
	 */
	USER_LOGIN_EXPIRED("A0230", "user login expired"),
	/**
	 * 用户验证码错误
	 */
	USER_VERIFICATION_CODE_ERROR("A0240", "user verification code error"),
	/**
	 * 用户验证码尝试次数超限
	 */
	USER_VERIFICATION_CODE_TRY_COUNT_OVER_LIMIT("A0241", "user verification code try count over limit"),
	/**
	 * 宏观错误
	 */
	ERROR("11111", "error"),
	;

	private final String code;
	private final String message;
}
