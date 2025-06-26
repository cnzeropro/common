package org.zero.common.data.constant;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/24
 */
public interface StringPool {
    /* ********************************************************** 标点符号 ********************************************************** */
    /**
     * 逗号：{@code ","}
     */
    String COMMA = ",";
    /**
     * 句号：{@code "."}
     */
    String PERIOD = ".";
    /**
     * 下划线：{@code "_"}
     */
    String UNDERLINE = "_";
    /**
     * 连接符：{@code "-"}
     */
    String HYPHEN = "-";
    /**
     * 冒号：{@code ":"}
     */
    String COLON = ":";
    /**
     * 双引号：{@code "\""}
     */
    String DOUBLE_QUOTE = "\"";
    /**
     * 单引号：{@code "'"}
     */
    String SINGLE_QUOTE = "'";
    /**
     * 引号：{@code "\""}
     */
    String QUOTATION = DOUBLE_QUOTE;
    /**
     * 撇号：{@code "'"}
     */
    String APOSTROPHE = SINGLE_QUOTE;
    /**
     * 反引号：{@code "`"}
     */
    String BACKTICK = "`";
    /**
     * 波浪号：{@code "~"}
     */
    String TILDE = "~";
    /**
     * 省略号：{@code "..."}
     */
    String ELLIPSIS = "...";
    /**
     * 问号：{@code "?"}
     */
    String QUESTION = "?";
    /**
     * 感叹号：{@code "!"}
     */
    String EXCLAMATION = "!";
    /**
     * 与号：{@code "&"}
     */
    String AMPERSAND = "&";
    /**
     * 星号：{@code "*"}
     */
    String ASTERISK = "*";
    /**
     * 分号：{@code ";"}
     */
    String SEMICOLON = ";";
    /**
     * 脱字符：{@code "^"}
     */
    String CARET = "^";
    /**
     * 花括号（左）：{@code "{"}
     */
    String CURLY_LEFT = "{";
    /**
     * 花括号（右）：<code>"}"</code>
     */
    String CURLY_RIGHT = "}";
    /**
     * 方括号（左）：{@code "["}
     */
    String SQUARE_LEFT = "[";
    /**
     * 方括号（右）：{@code "]"}
     */
    String SQUARE_RIGHT = "]";
    /**
     * 圆括号（左）：{@code "("}
     */
    String ROUND_LEFT = "(";
    /**
     * 圆括号（右）：{@code ")"}
     */
    String ROUND_RIGHT = ")";
    /**
     * 尖括号（左）：{@code "<"}
     */
    String ANGLE_LEFT = "<";
    /**
     * 尖括号（右）：{@code ">"}
     */
    String ANGLE_RIGHT = ">";

    /* ********************************************************** 货币符号 ********************************************************** */
    /**
     * 人民币（RMB）：{@code "¥"}
     */
    String YUAN = "¥";
    /**
     * 港币（HKD）：{@code "HK$"}
     */
    String HONGKONG_DOLLAR = "HK$";
    /**
     * 美元（USD）：{@code "$"}
     */
    String DOLLAR = "$";
    /**
     * 英镑（GBP）：{@code "£"}
     */
    String POUND_STERLING = "£";
    /**
     * 欧元（EUR）：{@code "€"}
     */
    String EURO = "€";
    /**
     * 韩元（KRW）：{@code "₩"}
     */
    String WON = "₩";
    /**
     * 日元（JPY）：{@code "¥"}
     */
    String YEN = YUAN;

    /* ********************************************************** 数学符号 ********************************************************** */
    /**
     * 加号：{@code "+"}
     */
    String PLUS = "+";
    /**
     * 减号：{@code "-"}
     */
    String MINUS = HYPHEN;
    /**
     * 乘号：{@code "*"}
     */
    String MULTIPLICATION = ASTERISK;
    /**
     * 除号：{@code "/"}
     */
    String DIVISION = "/";
    /**
     * 幂：{@code "^"}
     */
    String POWER = CARET;
    /**
     * 等号：{@code "="}
     */
    String EQUAL = "=";
    /**
     * 小于号：{@code "<"}
     */
    String LESS_THAN = ANGLE_LEFT;
    /**
     * 大于号：{@code ">"}
     */
    String GREATER_THAN = ANGLE_RIGHT;
    /**
     * 百分号：{@code "%"}
     */
    String PERCENT = "%";
    /**
     * 千分号：{@code "‰"}
     */
    String PER_MILLE = "‰";
    /**
     * 万分号：{@code "‱"}
     */
    String PER_MYRIAD = "‱";
    /**
     * 平方根：{@code "√"}
     */
    String SQUARE_ROOT = "√";
    /**
     * 角度：{@code "°"}
     */
    String ANGLE = "°";
    /**
     * 圆周率：{@code "π"}
     */
    String PI = "π";
    /**
     * 无穷号：{@code "∞"}
     */
    String INFINITY = "∞";

    /* ********************************************************** 编程符号 ********************************************************** */
    /**
     * 与：{@code "&"}
     */
    String AND = AMPERSAND;
    /**
     * 或：{@code "|"}
     */
    String OR = "|";
    /**
     * 非：{@code "!"}
     */
    String NOT = EXCLAMATION;
    /**
     * 异或：{@code "^"}
     */
    String XOR = CARET;
    /**
     * 逻辑与：{@code "&&"}
     */
    String LOGICAL_AND = "&&";
    /**
     * 逻辑或：{@code "||"}
     */
    String LOGICAL_OR = "||";
    /**
     * 左位移：{@code "<<"}
     */
    String LEFT_SHIFT = "<<";
    /**
     * 右位移：{@code ">>"}
     */
    String RIGHT_SHIFT = ">>";
    /**
     * 取模：{@code "%"}
     */
    String MOD = PERCENT;
    /**
     * 通配符：{@code "*"}
     */
    String WILDCARD = ASTERISK;
    /**
     * 短线：{@code "-"}
     */
    String DASHED = HYPHEN;
    /**
     * 点：{@code "."}
     */
    String DOT = PERIOD;
    /**
     * 双点：{@code ".."} <br>
     */
    String DOUBLE_DOT = "..";
    /**
     * 斜杠（正斜杠）：{@code "/"}
     */
    String SLASH = DIVISION;
    /**
     * 反斜杠：{@code "\\"}
     */
    String BACKSLASH = "\\";
    /**
     * 回车符：{@code "\r"} <br>
     * 解释：该字符常用于表示 Linux 系统和 MacOS 系统下的文本换行
     */
    String CR = "\r";
    /**
     * 换行符：{@code "\n"}
     */
    String LF = "\n";
    /**
     * 回车换行符：{@code "\r\n"} <br>
     * 解释：该字符串常用于表示 Windows 系统下的文本换行
     */
    String CRLF = "\r\n";
    /**
     * 制表符：{@code "\t"}
     */
    String TAB = "\t";
    /**
     * 艾特号：{@code "@"}
     */
    String AT = "@";
    /**
     * 井号：{@code "#"}
     */
    String POUND = "#";
    /**
     * 自增运算符：{@code "++"}
     */
    String INCREMENT = "++";
    /**
     * 自减运算符：{@code "--"}
     */
    String DECREMENT = "--";

    /* ********************************************************** 特殊符号 ********************************************************** */
    /**
     * 版权符号：{@code "©"}
     */
    String COPYRIGHT = "©";
    /**
     * 注册商标符号：{@code "®"}
     */
    String REGISTERED_TRADEMARK = "®";
    /**
     * 商标符号：{@code "™"}
     */
    String TRADEMARK = "™";
    /**
     * 男性标志：{@code "♂"}
     */
    String MALE = "♂";
    /**
     * 女性标志：{@code "♀"}
     */
    String FEMALE = "♀";

    /* ********************************************************** 其他字符 ********************************************************** */
    /**
     * 空字符串：{@code ""}
     */
    String EMPTY = "";
    /**
     * 空格符：{@code " "}
     */
    String SPACE = " ";
    /**
     * {@code "null"} <br>
     * 注意：{@code "null" != null}
     */
    String NULL = "null";
    /**
     * {@code "true"} <br>
     * 注意：{@code "true" != true}
     */
    String BOOL_TRUE = "true";
    /**
     * {@code "false"} <br>
     * 注意：{@code "false" != false}
     */
    String BOOL_FALSE = "false";
    /**
     * 空 JSON 对象：{@code "{}"}
     */
    String EMPTY_JSON_OBJECT = "{}";
    /**
     * 空 JSON 数组：{@code "[]"}
     */
    String EMPTY_JSON_ARRAY = "[]";
}
