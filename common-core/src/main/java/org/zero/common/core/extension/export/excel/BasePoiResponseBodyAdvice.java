package org.zero.common.core.extension.export.excel;

import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/20
 */
public abstract class BasePoiResponseBodyAdvice extends BaseExcelExportResponseBodyAdvice {
    protected BasePoiResponseBodyAdvice(String... packageNames) {
        super(packageNames);
    }

    protected BasePoiResponseBodyAdvice(int order, String... packageNames) {
        super(order, packageNames);
    }

    protected BasePoiResponseBodyAdvice(String regex) {
        super(regex);
    }

    protected BasePoiResponseBodyAdvice(int order, String regex) {
        super(order, regex);
    }

    protected void setWorkbookPassword(Workbook workbook, String password) {
        if (StringUtils.hasText(password)) {
            if (workbook instanceof XSSFWorkbook) {
                XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;
                xssfWorkbook.setWorkbookPassword(password, HashAlgorithm.sha512);
                xssfWorkbook.setRevisionsPassword(password, HashAlgorithm.md5);
            }
            if (workbook instanceof HSSFWorkbook) {
                HSSFWorkbook hssfWorkbook = (HSSFWorkbook) workbook;
                Biff8EncryptionKey.setCurrentUserPassword(password);
                hssfWorkbook.writeProtectWorkbook(password, "");
            }
            if (workbook instanceof SXSSFWorkbook) {
                SXSSFWorkbook sxssfWorkbook = (SXSSFWorkbook) workbook;
                XSSFWorkbook xssfWorkbook = sxssfWorkbook.getXSSFWorkbook();
                this.setWorkbookPassword(xssfWorkbook, password);
            }
        }
    }

    protected void setSheetPassword(Sheet sheet, String password) {
        if (StringUtils.hasText(password)) {
            sheet.protectSheet(password);
        }
    }
}
