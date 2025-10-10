package org.zero.common.core.support.timer;

import java.io.PrintStream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/26
 */
public class PrintTimer extends BaseTimer<PrintTimer> {
    protected PrintStream printStream = System.out;

    public static PrintTimer start() {
        return new PrintTimer();
    }

    public static PrintTimer start(String name) {
        return new PrintTimer(name);
    }

    public static PrintTimer start(PrintStream printStream) {
        return new PrintTimer(printStream);
    }

    public static PrintTimer start(String name, PrintStream printStream) {
        return new PrintTimer(name, printStream);
    }

    public PrintTimer print() {
        return this.consume((name, duration) -> printStream.printf("%s time consumption: %s%n", name, duration));
    }

    @Override
    public void close() {
        this.print().reset();
    }

    protected PrintTimer() {
        super();
    }

    protected PrintTimer(String name) {
        super(name);
    }

    protected PrintTimer(PrintStream printStream) {
        super();
        this.printStream = printStream;
    }


    protected PrintTimer(String name, PrintStream printStream) {
        super(name);
        this.printStream = printStream;
    }
}
