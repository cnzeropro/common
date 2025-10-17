package org.zero.common.core.aop.aspectj.aspect;

import java.time.Duration;

/**
 * 记录时间的切面
 * <p>
 * 注意：配置编译时织入插件
 * <pre>{@code
 * <plugin>
 *     <groupId>org.codehaus.mojo</groupId>
 *     <artifactId>aspectj-maven-plugin</artifactId>
 *     <version>1.15.0</version>
 *     <configuration>
 *         <complianceLevel>1.8</complianceLevel>
 *         <source>1.8</source>
 *         <target>1.8</target>
 *         <aspectLibraries>
 *             <aspectLibrary>
 *                 <groupId>org.springframework</groupId>
 *                 <artifactId>spring-aspects</artifactId>
 *             </aspectLibrary>
 *         </aspectLibraries>
 *     </configuration>
 *     <executions>
 *         <execution>
 *             <goals>
 *                 <goal>compile</goal>
 *             </goals>
 *         </execution>
 *     </executions>
 * </plugin>
 * }</pre>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/30
 */
public aspect TimeLoggerAspect {
    pointcut loggableMethods(): @annotation(org.zero.common.core.aop.aspectj.annotation.Loggable);

    Object around(): loggableMethods() {
        long startTime = System.nanoTime();
        try {
            return proceed();
        } finally {
            long endTime = System.nanoTime();
            String signatureName = thisJoinPoint.getSignature().getName();
            Duration duration = Duration.ofNanos(endTime - startTime);
            System.out.printf("Method[%s] execution time: %s%n", signatureName, duration);
        }
    }
}
