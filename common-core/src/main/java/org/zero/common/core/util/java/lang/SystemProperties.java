package org.zero.common.core.util.java.lang;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/29
 */
public interface SystemProperties {
    /**
     * Java 版本
     */
    String JAVA_VERSION = "java.version";
    /**
     * Java 安装目录
     */
    String JAVA_HOME = "java.home";
    /**
     * Java 虚拟机名称
     */
    String JAVA_VM_NAME = "java.vm.name";
    /**
     * Java 虚拟机版本
     */
    String JAVA_VM_VERSION = "java.vm.version";
    /**
     * Java 虚拟机厂商
     */
    String JAVA_VM_VENDOR = "java.vm.vendor";
    /**
     * Java 虚拟机规范名称
     */
    String JAVA_VM_SPECIFICATION_NAME = "java.vm.specification.name";
    /**
     * Java 虚拟机规范版本
     */
    String JAVA_VM_SPECIFICATION_VERSION = "java.vm.specification.version";
    /**
     * Java 虚拟机规范厂商
     */
    String JAVA_VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor";
    /**
     * Java 类路径
     */
    String JAVA_CLASS_PATH = "java.class.path";
    /**
     * Java 类版本
     */
    String JAVA_CLASS_VERSION = "java.class.version";
    /**
     * JRE 名称
     */
    String JAVA_RUNTIME_NAME = "java.runtime.name";
    /**
     * JRE 版本
     */
    String JAVA_RUNTIME_VERSION = "java.runtime.version";
    /**
     * Java 规范名称
     */
    String JAVA_SPECIFICATION_NAME = "java.specification.name";
    /**
     * Java 规范版本
     */
    String JAVA_SPECIFICATION_VERSION = "java.specification.version";
    /**
     * Java 规范厂商
     */
    String JAVA_SPECIFICATION_VENDOR = "java.specification.vendor";
    /**
     * Java 厂商名称
     */
    String JAVA_VENDOR = "java.vendor";
    /**
     * Java 厂商网址
     */
    String JAVA_VENDOR_URL = "java.vendor.url";
    /**
     * 临时文件目录
     */
    String JAVA_IO_TMPDIR = "java.io.tmpdir";
    /**
     * JVM 加载本地库（Native Library）的路径
     */
    String JAVA_LIBRARY_PATH = "java.library.path";

    /**
     * 操作系统名称
     */
    String OS_NAME = "os.name";
    /**
     * 操作系统版本
     */
    String OS_VERSION = "os.version";
    /**
     * 操作系统架构
     */
    String OS_ARCH = "os.arch";

    /**
     * 路径分隔符
     */
    String PATH_SEPARATOR = "path.separator";
    /**
     * 行分隔符
     */
    String LINE_SEPARATOR = "line.separator";
    /**
     * 文件分隔符
     */
    String FILE_SEPARATOR = "file.separator";
    /**
     * 文件编码
     */
    String FILE_ENCODING = "file.encoding";
    /**
     * 本地编码
     */
    String NATIVE_ENCODING = "native.encoding";
    /**
     * 标准输出编码
     */
    String STDOUT_ENCODING = "stdout.encoding";
    /**
     * 标准错误输出编码
     */
    String STDERR_ENCODING = "stderr.encoding";

    /**
     * 用户账户名称
     */
    String USER_NAME = "user.name";
    /**
     * 用户主目录
     */
    String USER_HOME = "user.home";
    /**
     * 用户工作目录
     */
    String USER_DIR = "user.dir";
    /**
     * 用户国家
     */
    String USER_COUNTRY = "user.country";
    /**
     * 用户语言
     */
    String USER_LANGUAGE = "user.language";
    /**
     * 用户时区
     */
    String USER_TIMEZONE = "user.timezone";
}
