package org.zero.common.core.extension.java;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/26
 */
public class PrintTimer extends BaseTimer<PrintTimer> {
    public static PrintTimer start() {
        return new PrintTimer();
    }

    public static PrintTimer start(String name) {
        return new PrintTimer(name);
    }

    public PrintTimer print() {
        return this.consume((name, duration) -> System.out.printf("%s time consumption: %s%n", name, duration));
    }

    @Override
    public void close()  {
        this.print().close();
    }

    protected PrintTimer() {
        super();
    }

    protected PrintTimer(String name) {
        super(name);
    }
}
