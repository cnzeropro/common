package org.zero.common.data.enumeration;

import org.zero.common.data.model.query.CacheStrategy;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/3
 */
public enum CacheControlInstruction implements CacheStrategy {
    NO_STORE {
        @Override
        public String getCacheControl() {
            return "no-store";
        }
    },
    NO_CACHE {
        @Override
        public String getCacheControl() {
            return "no-cache";
        }
    },
    NO_TRANSFORM {
        @Override
        public String getCacheControl() {
            return "no-transform";
        }
    },
    MUST_REVALIDATE {
        @Override
        public String getCacheControl() {
            return "must-revalidate";
        }
    },
    ;
}
