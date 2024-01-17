package org.zero.common.core.util.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.zero.common.core.util.spring.context.SpringContextUtils;

import java.util.Objects;

/**
 * @author zero
 * @since 2021/10/21
 */
@Slf4j
public class QuartzUtils {
    protected QuartzUtils() {
    }

    protected static final String GROUP_SUFFIX = "-group";
    protected static final String JOB_SUFFIX = "-job";
    protected static final String JOB_GROUP_SUFFIX = JOB_SUFFIX + GROUP_SUFFIX;
    protected static final String TRIGGER_SUFFIX = "-trigger";
    protected static final String TRIGGER_GROUP_SUFFIX = TRIGGER_SUFFIX + GROUP_SUFFIX;

    public static boolean scheduleJob(String key, Class<? extends Job> clazz, String cron) {
        return scheduleJob(key, clazz, cron, new JobDataMap());
    }

    public static boolean scheduleJob(String key, Class<? extends Job> clazz, String cron, JobDataMap jobDataMap) {
        return scheduleJob(key, clazz, createTrigger(key, cron), jobDataMap);
    }

    public static boolean scheduleJob(String key, Class<? extends Job> clazz, Trigger trigger) {
        return scheduleJob(key, clazz, trigger, new JobDataMap());
    }

    public static boolean scheduleJob(String key, Class<? extends Job> clazz, Trigger trigger, JobDataMap jobDataMap) {
        return scheduleJob(createJobDetail(key, clazz, jobDataMap), trigger);
    }

    public static boolean scheduleJob(Trigger trigger) {
        try {
            getScheduler().scheduleJob(trigger);
            return true;
        } catch (SchedulerException e) {
            log.warn(String.format("schedule job error: %s", trigger.getJobKey()), e);
            return false;
        }
    }

    public static boolean scheduleJob(JobDetail jobDetail, Trigger trigger) {
        try {
            getScheduler().scheduleJob(jobDetail, trigger);
            return true;
        } catch (SchedulerException e) {
            log.warn(String.format("schedule job error: %s", jobDetail.getKey()), e);
            return false;
        }
    }

    public static boolean rescheduleJob(String key, String cron) {
        return rescheduleJob(createTriggerKey(key), createTrigger(key, cron));
    }

    public static boolean rescheduleJob(TriggerKey triggerKey, Trigger trigger) {
        try {
            getScheduler().rescheduleJob(triggerKey, trigger);
            return true;
        } catch (SchedulerException e) {
            log.warn(String.format("reschedule job error: %s", trigger.getJobKey()), e);
            return false;
        }
    }

    public static boolean addJob(String key, Class<? extends Job> clazz) {
        return addJob(key, clazz, new JobDataMap());
    }

    public static boolean addJob(String key, Class<? extends Job> clazz, JobDataMap jobDataMap) {
        return addJob(createJobDetail(key, clazz, jobDataMap), true);
    }

    public static boolean addJob(JobDetail jobDetail, boolean replace) {
        try {
            getScheduler().addJob(jobDetail, replace);
            return true;
        } catch (SchedulerException e) {
            log.warn(String.format("add job error: %s", jobDetail.getKey()), e);
            return false;
        }
    }

    public static boolean triggerJob(String key) {
        return triggerJob(key, new JobDataMap());
    }

    public static boolean triggerJob(String key, JobDataMap jobDataMap) {
        return triggerJob(createJobKey(key), jobDataMap);
    }

    public static boolean triggerJob(JobKey jobKey, JobDataMap jobDataMap) {
        try {
            getScheduler().triggerJob(jobKey, jobDataMap);
            return true;
        } catch (SchedulerException e) {
            log.warn(String.format("trigger job error: %s", jobKey), e);
            return false;
        }
    }

    public static boolean checkExists(String key) {
        return checkExists(createJobKey(key));
    }

    public static boolean checkExists(JobKey jobKey) {
        try {
            return getScheduler().checkExists(jobKey);
        } catch (SchedulerException e) {
            log.warn(String.format("check job exists error: %s", jobKey), e);
            return false;
        }
    }

    public static boolean deleteJob(String key) {
        return deleteJob(createJobKey(key));
    }

    public static boolean deleteJob(JobKey jobKey) {
        try {
            return getScheduler().deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.warn(String.format("delete job error: %s", jobKey), e);
            return false;
        }
    }

    public static boolean pauseJob(String key) {
        return pauseJob(createJobKey(key));
    }

    public static boolean pauseJob(JobKey jobKey) {
        try {
            getScheduler().pauseJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            log.warn(String.format("pause job error: %s", jobKey), e);
            return false;
        }
    }

    public static boolean resumeJob(String key) {
        return resumeJob(createJobKey(key));
    }

    public static boolean resumeJob(JobKey jobKey) {
        try {
            getScheduler().resumeJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            log.warn(String.format("resume job error: %s", jobKey), e);
            return false;
        }
    }

    /* ********************************************* creating-related Methods ********************************************* */

    public static JobDetail createJobDetail(String key, Class<? extends Job> clazz) {
        return createJobDetail(key, clazz, new JobDataMap());
    }

    public static JobDetail createJobDetail(String key, Class<? extends Job> clazz, JobDataMap jobDataMap) {
        return createJobDetail(createJobKey(key), clazz, jobDataMap);
    }

    public static JobDetail createJobDetail(JobKey jobKey, Class<? extends Job> clazz, JobDataMap jobDataMap) {
        return JobBuilder.newJob()
                .ofType(clazz)
                .withIdentity(jobKey)
                // 使用给定描述
                // .withDescription("")
                .usingJobData(jobDataMap)
                // 调度器执行任务时，遇到'recovery'（恢复）或者'fail-over'（故障转移）重新执行
                .requestRecovery()
                // 持久化：没有触发器也不删除该任务
                .storeDurably()
                .build();
    }

    public static Trigger createTrigger(String key, String cron) {
        return createTrigger(key, cron, new JobDataMap());
    }

    public static Trigger createTrigger(String key, String cron, JobDataMap jobDataMap) {
        return createTrigger(createTriggerKey(key), CronScheduleBuilder.cronSchedule(cron), jobDataMap, null);
    }

    public static Trigger createTrigger(TriggerKey triggerKey, ScheduleBuilder<? extends Trigger> scheduleBuilder, JobDataMap jobDataMap,
                                        JobKey jobKey) {
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(scheduleBuilder)
                // 使用给定描述
                // .withDescription("")
                .withPriority(Trigger.DEFAULT_PRIORITY)
                // 从现在开始执行
                .startNow()
                // 在指定时间开始执行
                // .startAt(date)
                // 在指定时间结束执行
                // .endAt(date)
                .usingJobData(jobDataMap)
                // 关联到指定 job
                .forJob(jobKey)
                .build();
    }

    public static JobKey createJobKey(String key) {
        return new JobKey(key + JOB_SUFFIX, key + JOB_GROUP_SUFFIX);
    }

    public static JobKey createJobKey(String jobKey, String groupKey) {
        return new JobKey(jobKey + JOB_SUFFIX, groupKey + JOB_SUFFIX);
    }

    public static TriggerKey createTriggerKey(String key) {
        return new TriggerKey(key + TRIGGER_SUFFIX, key + TRIGGER_GROUP_SUFFIX);
    }

    public static TriggerKey createTriggerKey(String triggerKey, String groupKey) {
        return new TriggerKey(triggerKey + TRIGGER_SUFFIX, groupKey + TRIGGER_SUFFIX);
    }

    private static Scheduler scheduler;

    protected static Scheduler getScheduler() {
        if (Objects.isNull(scheduler)) {
            synchronized (QuartzUtils.class) {
                if (Objects.isNull(scheduler)) {
                    scheduler = SpringContextUtils.getBean(Scheduler.class);
                }
            }
        }
        return scheduler;
    }
}
