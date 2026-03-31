package org.zero.common.data.constant;

/**
 * 字符常量池。
 * <p>
 * 集中定义各类常用字符（标点、货币、数学、编程、特殊符号）的字面量常量，
 * 避免在业务代码中出现魔法字符（magic character）。部分语义重叠的字符通过别名引用同一字面量。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/24
 */
public interface CharPool {
    /* ********************************************************** 标点符号 ********************************************************** */
    /**
     * 逗号：{@code ','}
     */
    char COMMA = ',';
    /**
     * 句号：{@code '.'}
     */
    char PERIOD = '.';
    /**
     * 下划线：{@code '_'}
     */
    char UNDERSCORE = '_';
    /**
     * 连接符：{@code '-'}
     */
    char HYPHEN = '-';
    /**
     * 冒号：{@code ':'}
     */
    char COLON = ':';
    /**
     * 双引号：{@code '"'}
     */
    char DOUBLE_QUOTE = '"';
    /**
     * 单引号：{@code '\''}
     */
    char SINGLE_QUOTE = '\'';
    /**
     * 引号：{@code '"'}
     */
    char QUOTATION = DOUBLE_QUOTE;
    /**
     * 撇号：{@code '\''}
     */
    char APOSTROPHE = SINGLE_QUOTE;
    /**
     * 反引号：{@code '`'}
     */
    char BACKTICK = '`';
    /**
     * 波浪号：{@code '~'}
     */
    char TILDE = '~';
    /**
     * 问号：{@code '?'}
     */
    char QUESTION = '?';
    /**
     * 感叹号：{@code '!'}
     */
    char EXCLAMATION = '!';
    /**
     * 与号：{@code '&'}
     */
    char AMPERSAND = '&';
    /**
     * 星号：{@code '*'}
     */
    char ASTERISK = '*';
    /**
     * 分号：{@code ';'}
     */
    char SEMICOLON = ';';
    /**
     * 脱字符：{@code '^'}
     */
    char CARET = '^';
    /**
     * 花括号（左）：{@code '{'}
     */
    char CURLY_LEFT = '{';
    /**
     * 花括号（右）：{@code '}'}
     */
    char CURLY_RIGHT = '}';
    /**
     * 方括号（左）：{@code '['}
     */
    char SQUARE_LEFT = '[';
    /**
     * 方括号（右）：{@code ']'}
     */
    char SQUARE_RIGHT = ']';
    /**
     * 圆括号（左）：{@code '('}
     */
    char ROUND_LEFT = '(';
    /**
     * 圆括号（右）：{@code ')'}
     */
    char ROUND_RIGHT = ')';
    /**
     * 尖括号（左）：{@code '<'}
     */
    char ANGLE_LEFT = '<';
    /**
     * 尖括号（右）：{@code '>'}
     */
    char ANGLE_RIGHT = '>';

    /* ********************************************************** 货币符号 ********************************************************** */
    /**
     * 人民币（RMB）：{@code '¥'}
     */
    char YUAN = '¥';
    /**
     * 美元（USD）：{@code '$'}
     */
    char DOLLAR = '$';
    /**
     * 英镑（GBP）：{@code '£'}
     */
    char POUND_STERLING = '£';
    /**
     * 欧元（EUR）：{@code '€'}
     */
    char EURO = '€';
    /**
     * 韩元（KRW）：{@code '₩'}
     */
    char WON = '₩';
    /**
     * 日元（JPY）：{@code '¥'}
     */
    char YEN = YUAN;

    /* ********************************************************** 数学符号 ********************************************************** */
    /**
     * 加号：{@code '+'}
     */
    char PLUS = '+';
    /**
     * 减号：{@code '-'}
     */
    char MINUS = HYPHEN;
    /**
     * 乘号：{@code '*'}
     */
    char MULTIPLICATION = ASTERISK;
    /**
     * 除号：{@code '/'}
     */
    char DIVISION = '/';
    /**
     * 幂：{@code '^'}
     */
    char POWER = CARET;
    /**
     * 等号：{@code '='}
     */
    char EQUAL = '=';
    /**
     * 小于号：{@code '<'}
     */
    char LESS_THAN = ANGLE_LEFT;
    /**
     * 大于号：{@code '>'}
     */
    char GREATER_THAN = ANGLE_RIGHT;
    /**
     * 百分号：{@code '%'}
     */
    char PERCENT = '%';
    /**
     * 千分号：{@code '‰'}
     */
    char PER_MILLE = '‰';
    /**
     * 无穷号：{@code '∞'}
     */
    char INFINITY = '∞';

    /* ********************************************************** 编程符号 ********************************************************** */
    /**
     * 与：{@code '&'}
     */
    char AND = AMPERSAND;
    /**
     * 或：{@code '|'}
     */
    char OR = '|';
    /**
     * 非：{@code '!'}
     */
    char NOT = EXCLAMATION;
    /**
     * 异或：{@code '^'}
     */
    char XOR = CARET;
    /**
     * 取模：{@code '%'}
     */
    char MOD = PERCENT;
    /**
     * 通配符：{@code '*'}
     */
    char WILDCARD = ASTERISK;
    /**
     * 短线：{@code '-'}
     */
    char DASH = HYPHEN;
    /**
     * 点：{@code '.'}
     */
    char DOT = PERIOD;
    /**
     * 斜杠（正斜杠）：{@code '/'}
     */
    char SLASH = DIVISION;
    /**
     * 反斜杠：{@code '\\'}
     */
    char BACKSLASH = '\\';
    /**
     * 回车符（Carriage Return）：{@code '\r'} <br>
     * 解释：通常与 {@link #LF} 组合为 {@code \r\n} 用于 Windows 系统的文本换行；
     * 旧版 Mac OS（9 及之前）单独使用 {@code \r} 换行
     */
    char CR = '\r';
    /**
     * 换行符：{@code '\n'}
     */
    char LF = '\n';
    /**
     * 制表符：{@code '\t'}
     */
    char TAB = '\t';
    /**
     * 艾特号：{@code '@'}
     */
    char AT = '@';
    /**
     * 井号：{@code '#'}
     */
    char POUND = '#';

    /* ********************************************************** 特殊符号 ********************************************************** */
    /**
     * 版权符号：{@code '©'}
     */
    char COPYRIGHT = '©';
    /**
     * 注册商标符号：{@code '®'}
     */
    char REGISTERED_TRADEMARK = '®';
    /**
     * 商标符号：{@code '™'}
     */
    char TRADEMARK = '™';
    /**
     * 男性标志：{@code '♂'}
     */
    char MALE = '♂';
    /**
     * 女性标志：{@code '♀'}
     */
    char FEMALE = '♀';

    /* ********************************************************** 其他字符 ********************************************************** */
    /**
     * 空字符：{@code ''}
     */
    char EMPTY = '\0';
    /**
     * 空格符：{@code ' '}
     */
    char SPACE = ' ';
}
