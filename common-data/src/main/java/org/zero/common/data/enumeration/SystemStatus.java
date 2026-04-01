package org.zero.common.data.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 系统状态枚举。
 * <p>
 * 基于《Java开发手册（黄山版）》附 3“错误码列表”整理。
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/12/1
 */
@Getter
@RequiredArgsConstructor
public enum SystemStatus implements Status {
	/* ************************ 通用状态 ************************ */
	/**
	 * 一切 ok
	 */
	OK("00000", "ok"),

	/* ************************ A0000 用户端错误 ************************ */
	/**
	 * 用户端错误
	 */
	CLIENT_ERROR("A0001", "client error"),

	/* ************************ A0100 用户注册错误 ************************ */
	/**
	 * 用户注册错误
	 */
	USER_REGISTRATION_ERROR("A0100", "user registration error"),
	/**
	 * 用户未同意隐私协议
	 */
	PRIVACY_POLICY_NOT_ACCEPTED("A0101", "privacy policy not accepted"),
	/**
	 * 注册国家或地区受限
	 */
	REGISTRATION_RESTRICTED_BY_COUNTRY_OR_REGION("A0102", "registration restricted by country or region"),
	/**
	 * 用户名校验失败
	 */
	USERNAME_CHECK_FAILED("A0110", "username check failed"),
	/**
	 * 用户名已存在
	 */
	USERNAME_ALREADY_EXISTS("A0111", "username already exists"),
	/**
	 * 用户名包含敏感词
	 */
	USERNAME_CONTAINS_SENSITIVE_WORDS("A0112", "username contains sensitive words"),
	/**
	 * 用户名包含特殊字符
	 */
	USERNAME_CONTAINS_SPECIAL_CHARACTERS("A0113", "username contains special characters"),
	/**
	 * 密码校验失败
	 */
	PASSWORD_CHECK_FAILED("A0120", "password check failed"),
	/**
	 * 密码长度不够
	 */
	PASSWORD_TOO_SHORT("A0121", "password too short"),
	/**
	 * 密码强度不够
	 */
	PASSWORD_TOO_WEAK("A0122", "password too weak"),
	/**
	 * 校验码输入错误
	 */
	VERIFICATION_CODE_INVALID("A0130", "invalid verification code"),
	/**
	 * 短信校验码输入错误
	 */
	SMS_VERIFICATION_CODE_INVALID("A0131", "invalid sms verification code"),
	/**
	 * 邮件校验码输入错误
	 */
	EMAIL_VERIFICATION_CODE_INVALID("A0132", "invalid email verification code"),
	/**
	 * 语音校验码输入错误
	 */
	VOICE_VERIFICATION_CODE_INVALID("A0133", "invalid voice verification code"),
	/**
	 * 用户证件异常
	 */
	USER_IDENTITY_INVALID("A0140", "invalid user identity"),
	/**
	 * 用户证件类型未选择
	 */
	USER_IDENTITY_TYPE_NOT_SELECTED("A0141", "user identity type not selected"),
	/**
	 * 大陆身份证编号校验非法
	 */
	MAINLAND_CHINA_ID_NUMBER_INVALID("A0142", "invalid mainland China ID number"),
	/**
	 * 护照编号校验非法
	 */
	PASSPORT_NUMBER_INVALID("A0143", "invalid passport number"),
	/**
	 * 军官证编号校验非法
	 */
	MILITARY_ID_NUMBER_INVALID("A0144", "invalid military ID number"),
	/**
	 * 用户基本信息校验失败
	 */
	USER_BASIC_INFO_CHECK_FAILED("A0150", "user basic information check failed"),
	/**
	 * 手机格式校验失败
	 */
	MOBILE_NUMBER_FORMAT_INVALID("A0151", "invalid mobile number format"),
	/**
	 * 地址格式校验失败
	 */
	ADDRESS_FORMAT_INVALID("A0152", "invalid address format"),
	/**
	 * 邮箱格式校验失败
	 */
	EMAIL_FORMAT_INVALID("A0153", "invalid email format"),

	/* ************************ A0200 用户登录异常 ************************ */
	/**
	 * 用户登录异常
	 */
	USER_LOGIN_ERROR("A0200", "user login error"),
	/**
	 * 用户账户不存在
	 */
	USER_ACCOUNT_NOT_FOUND("A0201", "user account not found"),
	/**
	 * 用户账户被冻结
	 */
	USER_ACCOUNT_FROZEN("A0202", "user account is frozen"),
	/**
	 * 用户账户已作废
	 */
	USER_ACCOUNT_INVALID("A0203", "user account is invalid"),
	/**
	 * 用户密码错误
	 */
	USER_PASSWORD_INCORRECT("A0210", "incorrect password"),
	/**
	 * 用户输入密码错误次数超限
	 */
	USER_PASSWORD_RETRY_LIMIT_EXCEEDED("A0211", "password retry limit exceeded"),
	/**
	 * 用户身份校验失败
	 */
	USER_IDENTITY_CHECK_FAILED("A0220", "user identity verification failed"),
	/**
	 * 用户指纹识别失败
	 */
	USER_FINGERPRINT_VERIFICATION_FAILED("A0221", "fingerprint verification failed"),
	/**
	 * 用户面容识别失败
	 */
	USER_FACE_VERIFICATION_FAILED("A0222", "face verification failed"),
	/**
	 * 用户未获得第三方登录授权
	 */
	USER_THIRD_PARTY_LOGIN_NOT_AUTHORIZED("A0223", "third-party login not authorized"),
	/**
	 * 用户登录已过期
	 */
	USER_LOGIN_SESSION_EXPIRED("A0230", "login session expired"),
	/**
	 * 用户验证码错误
	 */
	USER_VERIFICATION_CODE_INVALID("A0240", "invalid user verification code"),
	/**
	 * 用户验证码尝试次数超限
	 */
	USER_VERIFICATION_CODE_ATTEMPT_LIMIT_EXCEEDED("A0241", "verification code attempt limit exceeded"),

	/* ************************ A0300 访问权限异常 ************************ */
	/**
	 * 访问权限异常
	 */
	ACCESS_PERMISSION_ERROR("A0300", "access permission error"),
	/**
	 * 访问未授权
	 */
	ACCESS_UNAUTHORIZED("A0301", "unauthorized access"),
	/**
	 * 正在授权中
	 */
	AUTHORIZATION_IN_PROGRESS("A0302", "authorization in progress"),
	/**
	 * 用户授权申请被拒绝
	 */
	USER_AUTHORIZATION_REQUEST_REJECTED("A0303", "user authorization request rejected"),
	/**
	 * 因访问对象隐私设置被拦截
	 */
	ACCESS_BLOCKED_BY_PRIVACY_SETTINGS("A0310", "access blocked by privacy settings"),
	/**
	 * 授权已过期
	 */
	AUTHORIZATION_EXPIRED("A0311", "authorization expired"),
	/**
	 * 无权限使用 API
	 */
	API_ACCESS_FORBIDDEN("A0312", "API access forbidden"),
	/**
	 * 用户访问被拦截
	 */
	USER_ACCESS_BLOCKED("A0320", "user access blocked"),
	/**
	 * 黑名单用户
	 */
	BLACKLISTED_USER("A0321", "blacklisted user"),
	/**
	 * 账号被冻结
	 */
	ACCESS_ACCOUNT_FROZEN("A0322", "account is frozen"),
	/**
	 * 非法 IP 地址
	 */
	ILLEGAL_IP_ADDRESS("A0323", "illegal IP address"),
	/**
	 * 网关访问受限
	 */
	GATEWAY_ACCESS_RESTRICTED("A0324", "gateway access restricted"),
	/**
	 * 地域黑名单
	 */
	REGION_BLACKLISTED("A0325", "region blacklisted"),
	/**
	 * 服务已欠费
	 */
	SERVICE_IN_ARREARS("A0330", "service in arrears"),
	/**
	 * 用户签名异常
	 */
	USER_SIGNATURE_ERROR("A0340", "user signature error"),
	/**
	 * RSA 签名错误
	 */
	RSA_SIGNATURE_ERROR("A0341", "RSA signature error"),

	/* ************************ A0400 用户请求参数错误 ************************ */
	/**
	 * 用户请求参数错误
	 */
	USER_REQUEST_PARAMETER_ERROR("A0400", "user request parameter error"),
	/**
	 * 包含非法恶意跳转链接
	 */
	ILLEGAL_REDIRECT_LINK_DETECTED("A0401", "illegal redirect link detected"),
	/**
	 * 无效的用户输入
	 */
	INVALID_USER_INPUT("A0402", "invalid user input"),
	/**
	 * 请求必填参数为空
	 */
	REQUIRED_PARAMETER_MISSING("A0410", "required parameter missing"),
	/**
	 * 用户订单号为空
	 */
	USER_ORDER_NUMBER_MISSING("A0411", "user order number missing"),
	/**
	 * 订购数量为空
	 */
	ORDER_QUANTITY_MISSING("A0412", "order quantity missing"),
	/**
	 * 缺少时间戳参数
	 */
	TIMESTAMP_PARAMETER_MISSING("A0413", "timestamp parameter missing"),
	/**
	 * 非法的时间戳参数
	 */
	INVALID_TIMESTAMP_PARAMETER("A0414", "invalid timestamp parameter"),
	/**
	 * 请求参数值超出允许的范围
	 */
	PARAMETER_VALUE_OUT_OF_RANGE("A0420", "parameter value out of range"),
	/**
	 * 参数格式不匹配
	 */
	PARAMETER_FORMAT_MISMATCH("A0421", "parameter format mismatch"),
	/**
	 * 地址不在服务范围
	 */
	ADDRESS_OUT_OF_SERVICE_RANGE("A0422", "address out of service range"),
	/**
	 * 时间不在服务范围
	 */
	TIME_OUT_OF_SERVICE_RANGE("A0423", "time out of service range"),
	/**
	 * 金额超出限制
	 */
	AMOUNT_EXCEEDS_LIMIT("A0424", "amount exceeds limit"),
	/**
	 * 数量超出限制
	 */
	QUANTITY_EXCEEDS_LIMIT("A0425", "quantity exceeds limit"),
	/**
	 * 请求批量处理总个数超出限制
	 */
	BATCH_TOTAL_EXCEEDS_LIMIT("A0426", "batch total exceeds limit"),
	/**
	 * 请求 JSON 解析失败
	 */
	REQUEST_JSON_PARSE_FAILED("A0427", "request JSON parse failed"),
	/**
	 * 用户输入内容非法
	 */
	ILLEGAL_USER_INPUT_CONTENT("A0430", "illegal user input content"),
	/**
	 * 包含违禁敏感词
	 */
	CONTENT_CONTAINS_PROHIBITED_SENSITIVE_WORDS("A0431", "content contains prohibited sensitive words"),
	/**
	 * 图片包含违禁信息
	 */
	IMAGE_CONTAINS_PROHIBITED_INFORMATION("A0432", "image contains prohibited information"),
	/**
	 * 文件侵犯版权
	 */
	FILE_INFRINGES_COPYRIGHT("A0433", "file infringes copyright"),
	/**
	 * 用户操作异常
	 */
	USER_OPERATION_ERROR("A0440", "user operation error"),
	/**
	 * 用户支付超时
	 */
	USER_PAYMENT_TIMEOUT("A0441", "user payment timeout"),
	/**
	 * 确认订单超时
	 */
	ORDER_CONFIRMATION_TIMEOUT("A0442", "order confirmation timeout"),
	/**
	 * 订单已关闭
	 */
	ORDER_CLOSED("A0443", "order closed"),

	/* ************************ A0500 用户请求服务异常 ************************ */
	/**
	 * 用户请求服务异常
	 */
	USER_REQUEST_SERVICE_ERROR("A0500", "user request service error"),
	/**
	 * 请求次数超出限制
	 */
	REQUEST_COUNT_LIMIT_EXCEEDED("A0501", "request count limit exceeded"),
	/**
	 * 请求并发数超出限制
	 */
	REQUEST_CONCURRENCY_LIMIT_EXCEEDED("A0502", "request concurrency limit exceeded"),
	/**
	 * 用户操作请等待
	 */
	USER_OPERATION_PENDING("A0503", "user operation pending, please wait"),
	/**
	 * WebSocket 连接异常
	 */
	WEBSOCKET_CONNECTION_ERROR("A0504", "WebSocket connection error"),
	/**
	 * WebSocket 连接断开
	 */
	WEBSOCKET_CONNECTION_DISCONNECTED("A0505", "WebSocket connection disconnected"),
	/**
	 * 用户重复请求
	 */
	DUPLICATE_USER_REQUEST("A0506", "duplicate user request"),

	/* ************************ A0600 用户资源异常 ************************ */
	/**
	 * 用户资源异常
	 */
	USER_RESOURCE_ERROR("A0600", "user resource error"),
	/**
	 * 账户余额不足
	 */
	ACCOUNT_BALANCE_INSUFFICIENT("A0601", "insufficient account balance"),
	/**
	 * 用户磁盘空间不足
	 */
	USER_DISK_SPACE_INSUFFICIENT("A0602", "insufficient user disk space"),
	/**
	 * 用户内存空间不足
	 */
	USER_MEMORY_SPACE_INSUFFICIENT("A0603", "insufficient user memory space"),
	/**
	 * 用户 OSS 容量不足
	 */
	USER_OSS_CAPACITY_INSUFFICIENT("A0604", "insufficient user OSS capacity"),
	/**
	 * 用户配额已用光
	 */
	USER_QUOTA_EXHAUSTED("A0605", "user quota exhausted"),

	/* ************************ A0700 用户上传文件异常 ************************ */
	/**
	 * 用户上传文件异常
	 */
	USER_FILE_UPLOAD_ERROR("A0700", "user file upload error"),
	/**
	 * 用户上传文件类型不匹配
	 */
	UPLOADED_FILE_TYPE_MISMATCH("A0701", "uploaded file type mismatch"),
	/**
	 * 用户上传文件太大
	 */
	UPLOADED_FILE_TOO_LARGE("A0702", "uploaded file too large"),
	/**
	 * 用户上传图片太大
	 */
	UPLOADED_IMAGE_TOO_LARGE("A0703", "uploaded image too large"),
	/**
	 * 用户上传视频太大
	 */
	UPLOADED_VIDEO_TOO_LARGE("A0704", "uploaded video too large"),
	/**
	 * 用户上传压缩文件太大
	 */
	UPLOADED_ARCHIVE_TOO_LARGE("A0705", "uploaded archive too large"),

	/* ************************ A0800 用户当前版本异常 ************************ */
	/**
	 * 用户当前版本异常
	 */
	USER_CURRENT_VERSION_ERROR("A0800", "user current version error"),
	/**
	 * 用户安装版本与系统不匹配
	 */
	INSTALLED_VERSION_INCOMPATIBLE_WITH_SYSTEM("A0801", "installed version incompatible with system"),
	/**
	 * 用户安装版本过低
	 */
	INSTALLED_VERSION_TOO_LOW("A0802", "installed version too low"),
	/**
	 * 用户安装版本过高
	 */
	INSTALLED_VERSION_TOO_HIGH("A0803", "installed version too high"),
	/**
	 * 用户安装版本已过期
	 */
	INSTALLED_VERSION_EXPIRED("A0804", "installed version expired"),
	/**
	 * 用户 API 请求版本不匹配
	 */
	API_REQUEST_VERSION_MISMATCH("A0805", "API request version mismatch"),
	/**
	 * 用户 API 请求版本过高
	 */
	API_REQUEST_VERSION_TOO_HIGH("A0806", "API request version too high"),
	/**
	 * 用户 API 请求版本过低
	 */
	API_REQUEST_VERSION_TOO_LOW("A0807", "API request version too low"),

	/* ************************ A0900 用户隐私未授权 ************************ */
	/**
	 * 用户隐私未授权
	 */
	USER_PRIVACY_NOT_AUTHORIZED("A0900", "user privacy not authorized"),
	/**
	 * 用户隐私未签署
	 */
	USER_PRIVACY_AGREEMENT_NOT_SIGNED("A0901", "user privacy agreement not signed"),
	/**
	 * 用户摄像头未授权
	 */
	USER_VIDEO_CAMERA_NOT_AUTHORIZED("A0902", "video camera access not authorized"),
	/**
	 * 用户相机未授权
	 */
	USER_CAMERA_NOT_AUTHORIZED("A0903", "camera access not authorized"),
	/**
	 * 用户图片库未授权
	 */
	USER_PHOTO_LIBRARY_NOT_AUTHORIZED("A0904", "photo library access not authorized"),
	/**
	 * 用户文件未授权
	 */
	USER_FILE_ACCESS_NOT_AUTHORIZED("A0905", "file access not authorized"),
	/**
	 * 用户位置信息未授权
	 */
	USER_LOCATION_NOT_AUTHORIZED("A0906", "location access not authorized"),
	/**
	 * 用户通讯录未授权
	 */
	USER_CONTACTS_NOT_AUTHORIZED("A0907", "contacts access not authorized"),

	/* ************************ A1000 用户设备异常 ************************ */
	/**
	 * 用户设备异常
	 */
	USER_DEVICE_ERROR("A1000", "user device error"),
	/**
	 * 用户相机异常
	 */
	USER_CAMERA_ERROR("A1001", "user camera error"),
	/**
	 * 用户麦克风异常
	 */
	USER_MICROPHONE_ERROR("A1002", "user microphone error"),
	/**
	 * 用户听筒异常
	 */
	USER_EARPIECE_ERROR("A1003", "user earpiece error"),
	/**
	 * 用户扬声器异常
	 */
	USER_SPEAKER_ERROR("A1004", "user speaker error"),
	/**
	 * 用户 GPS 定位异常
	 */
	USER_GPS_ERROR("A1005", "user GPS error"),

	/* ************************ B0000 系统执行出错 ************************ */
	/**
	 * 系统执行出错
	 */
	SYSTEM_EXECUTION_ERROR("B0001", "system execution error"),

	/* ************************ B0100 系统执行超时 ************************ */
	/**
	 * 系统执行超时
	 */
	SYSTEM_EXECUTION_TIMEOUT("B0100", "system execution timeout"),
	/**
	 * 系统订单处理超时
	 */
	SYSTEM_ORDER_PROCESSING_TIMEOUT("B0101", "system order processing timeout"),

	/* ************************ B0200 系统容灾功能被触发 ************************ */
	/**
	 * 系统容灾功能被触发
	 */
	SYSTEM_DISASTER_RECOVERY_TRIGGERED("B0200", "system disaster recovery triggered"),
	/**
	 * 系统限流
	 */
	SYSTEM_RATE_LIMITED("B0210", "system rate limited"),
	/**
	 * 系统功能降级
	 */
	SYSTEM_DEGRADED("B0220", "system degraded"),

	/* ************************ B0300 系统资源异常 ************************ */
	/**
	 * 系统资源异常
	 */
	SYSTEM_RESOURCE_ERROR("B0300", "system resource error"),
	/**
	 * 系统资源耗尽
	 */
	SYSTEM_RESOURCE_EXHAUSTED("B0310", "system resources exhausted"),
	/**
	 * 系统磁盘空间耗尽
	 */
	SYSTEM_DISK_SPACE_EXHAUSTED("B0311", "system disk space exhausted"),
	/**
	 * 系统内存耗尽
	 */
	SYSTEM_MEMORY_EXHAUSTED("B0312", "system memory exhausted"),
	/**
	 * 文件句柄耗尽
	 */
	FILE_HANDLES_EXHAUSTED("B0313", "file handles exhausted"),
	/**
	 * 系统连接池耗尽
	 */
	SYSTEM_CONNECTION_POOL_EXHAUSTED("B0314", "system connection pool exhausted"),
	/**
	 * 系统线程池耗尽
	 */
	SYSTEM_THREAD_POOL_EXHAUSTED("B0315", "system thread pool exhausted"),
	/**
	 * 系统资源访问异常
	 */
	SYSTEM_RESOURCE_ACCESS_ERROR("B0320", "system resource access error"),
	/**
	 * 系统读取磁盘文件失败
	 */
	SYSTEM_DISK_FILE_READ_FAILED("B0321", "system disk file read failed"),

	/* ************************ C0000 调用第三方服务出错 ************************ */
	/**
	 * 调用第三方服务出错
	 */
	THIRD_PARTY_SERVICE_ERROR("C0001", "third-party service error"),

	/* ************************ C0100 中间件服务出错 ************************ */
	/**
	 * 中间件服务出错
	 */
	MIDDLEWARE_SERVICE_ERROR("C0100", "middleware service error"),
	/**
	 * RPC 服务出错
	 */
	RPC_SERVICE_ERROR("C0110", "RPC service error"),
	/**
	 * RPC 服务未找到
	 */
	RPC_SERVICE_NOT_FOUND("C0111", "RPC service not found"),
	/**
	 * RPC 服务未注册
	 */
	RPC_SERVICE_NOT_REGISTERED("C0112", "RPC service not registered"),
	/**
	 * 接口不存在
	 */
	INTERFACE_NOT_FOUND("C0113", "interface not found"),
	/**
	 * 消息服务出错
	 */
	MESSAGE_SERVICE_ERROR("C0120", "message service error"),
	/**
	 * 消息投递出错
	 */
	MESSAGE_DELIVERY_ERROR("C0121", "message delivery error"),
	/**
	 * 消息消费出错
	 */
	MESSAGE_CONSUMPTION_ERROR("C0122", "message consumption error"),
	/**
	 * 消息订阅出错
	 */
	MESSAGE_SUBSCRIPTION_ERROR("C0123", "message subscription error"),
	/**
	 * 消息分组未查到
	 */
	MESSAGE_GROUP_NOT_FOUND("C0124", "message group not found"),
	/**
	 * 缓存服务出错
	 */
	CACHE_SERVICE_ERROR("C0130", "cache service error"),
	/**
	 * key 长度超过限制
	 */
	CACHE_KEY_LENGTH_EXCEEDS_LIMIT("C0131", "cache key length exceeds limit"),
	/**
	 * value 长度超过限制
	 */
	CACHE_VALUE_LENGTH_EXCEEDS_LIMIT("C0132", "cache value length exceeds limit"),
	/**
	 * 存储容量已满
	 */
	STORAGE_CAPACITY_FULL("C0133", "storage capacity full"),
	/**
	 * 不支持的数据格式
	 */
	UNSUPPORTED_DATA_FORMAT("C0134", "unsupported data format"),
	/**
	 * 配置服务出错
	 */
	CONFIGURATION_SERVICE_ERROR("C0140", "configuration service error"),
	/**
	 * 网络资源服务出错
	 */
	NETWORK_RESOURCE_SERVICE_ERROR("C0150", "network resource service error"),
	/**
	 * VPN 服务出错
	 */
	VPN_SERVICE_ERROR("C0151", "VPN service error"),
	/**
	 * CDN 服务出错
	 */
	CDN_SERVICE_ERROR("C0152", "CDN service error"),
	/**
	 * 域名解析服务出错
	 */
	DNS_SERVICE_ERROR("C0153", "DNS service error"),
	/**
	 * 网关服务出错
	 */
	GATEWAY_SERVICE_ERROR("C0154", "gateway service error"),

	/* ************************ C0200 第三方系统执行超时 ************************ */
	/**
	 * 第三方系统执行超时
	 */
	THIRD_PARTY_SYSTEM_TIMEOUT("C0200", "third-party system timeout"),
	/**
	 * RPC 执行超时
	 */
	RPC_EXECUTION_TIMEOUT("C0210", "RPC execution timeout"),
	/**
	 * 消息投递超时
	 */
	MESSAGE_DELIVERY_TIMEOUT("C0220", "message delivery timeout"),
	/**
	 * 缓存服务超时
	 */
	CACHE_SERVICE_TIMEOUT("C0230", "cache service timeout"),
	/**
	 * 配置服务超时
	 */
	CONFIGURATION_SERVICE_TIMEOUT("C0240", "configuration service timeout"),
	/**
	 * 数据库服务超时
	 */
	DATABASE_SERVICE_TIMEOUT("C0250", "database service timeout"),

	/* ************************ C0300 数据库服务出错 ************************ */
	/**
	 * 数据库服务出错
	 */
	DATABASE_SERVICE_ERROR("C0300", "database service error"),
	/**
	 * 表不存在
	 */
	TABLE_NOT_FOUND("C0311", "table not found"),
	/**
	 * 列不存在
	 */
	COLUMN_NOT_FOUND("C0312", "column not found"),
	/**
	 * 多表关联中存在多个相同名称的列
	 */
	DUPLICATE_COLUMN_NAMES_IN_JOIN("C0321", "duplicate column names in join"),
	/**
	 * 数据库死锁
	 */
	DATABASE_DEADLOCK("C0331", "database deadlock"),
	/**
	 * 主键冲突
	 */
	PRIMARY_KEY_CONFLICT("C0341", "primary key conflict"),

	/* ************************ C0400 第三方容灾系统被触发 ************************ */
	/**
	 * 第三方容灾系统被触发
	 */
	THIRD_PARTY_DISASTER_RECOVERY_TRIGGERED("C0400", "third-party disaster recovery triggered"),
	/**
	 * 第三方系统限流
	 */
	THIRD_PARTY_RATE_LIMITED("C0401", "third-party rate limited"),
	/**
	 * 第三方功能降级
	 */
	THIRD_PARTY_FUNCTION_DEGRADED("C0402", "third-party function degraded"),

	/* ************************ C0500 通知服务出错 ************************ */
	/**
	 * 通知服务出错
	 */
	NOTIFICATION_SERVICE_ERROR("C0500", "notification service error"),
	/**
	 * 短信提醒服务失败
	 */
	SMS_NOTIFICATION_SERVICE_FAILED("C0501", "SMS notification service failed"),
	/**
	 * 语音提醒服务失败
	 */
	VOICE_NOTIFICATION_SERVICE_FAILED("C0502", "voice notification service failed"),
	/**
	 * 邮件提醒服务失败
	 */
	EMAIL_NOTIFICATION_SERVICE_FAILED("C0503", "email notification service failed"),

	/* ************************ 通用兜底状态 ************************ */
	/**
	 * 宏观错误
	 */
	ERROR("11111", "error"),
	;

	private final String code;
	private final String message;
}
