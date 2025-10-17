package org.zero.common.core.extension.javax.validation;

import javax.validation.groups.Default;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2021/3/16
 */
public interface Base extends Default {
    interface Crud extends Base {
        interface Create extends Crud {
        }

        interface Read extends Crud {
        }

        interface Update extends Crud {
        }

        interface Delete extends Crud {
        }
    }
}
