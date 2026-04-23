package com.canteen.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 供餐日字符串 yyyy-MM-dd（与运行本服务的 JVM 默认时区一致，便于与本地库中 CURDATE 种子对齐）
 */
public final class DateUtils {

    private DateUtils() {
    }

    public static String tomorrowYyyyMmDd() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    public static String nowYyyyMmDdHhMmSs() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
