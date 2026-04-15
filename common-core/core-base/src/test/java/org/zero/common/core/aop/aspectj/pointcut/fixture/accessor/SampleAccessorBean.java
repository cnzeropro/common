package org.zero.common.core.aop.aspectj.pointcut.fixture.accessor;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
public class SampleAccessorBean {
	private String name;
	private boolean enabled;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void execute() {
	}
}
