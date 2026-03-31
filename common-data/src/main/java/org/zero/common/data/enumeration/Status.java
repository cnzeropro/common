package org.zero.common.data.enumeration;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

/**
 * 基础状态。
 *
 * @author Zero
 * @since 2021/8/24
 */
public interface Status extends Serializable {
	String OK_CODE = "00000";
	String ERROR_CODE = "11111";

	Serializable getCode();

	CharSequence getMessage();

	default boolean isOk() {
		return Objects.equals(this.getCode(), OK_CODE);
	}

	@Getter
	class Default implements Status {
		public static final Status OK = new Default(OK_CODE, "ok");
		public static final Status ERROR = new Default(ERROR_CODE, "error");

		protected final Serializable code;
		protected final CharSequence message;

		protected Default(Serializable code, CharSequence message) {
			this.code = code;
			this.message = message;
		}

		public static Default of(Serializable code, CharSequence message) {
			return new Default(code, message);
		}
	}
}
