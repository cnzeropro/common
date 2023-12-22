package org.zero.common.core.util.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
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
        return scheduleJob(key, clazz, createCronTrigger(key, cron));
    }

    public static boolean scheduleJob(String key, Class<? extends Job> clazz, JobDataMap jobDataMap, String cron) {
        return scheduleJob(key, clazz, jobDataMap, createCronTrigger(key, cron));
    }

    public static boolean scheduleJob(String key, Class<? extends Job> clazz, Trigger trigger) {
        return scheduleJob(createJobDetail(key, clazz), trigger);
    }

    public static boolean scheduleJob(String key, Class<? extends Job> clazz, JobDataMap jobDataMap, Trigger trigger) {
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
        return rescheduleJob(createTriggerKey(key), createCronTrigger(key, cron));
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
        JobDetail jobDetail = createJobDetail(key, clazz, jobDataMap);
        try {
            getScheduler().addJob(jobDetail, true);
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
        JobKey jobKey = createJobKey(key);
        try {
            getScheduler().triggerJob(jobKey, jobDataMap);
            return true;
        } catch (SchedulerException e) {
            log.warn(String.format("trigger job error: %s", jobKey), e);
            return false;
        }
    }

    public static boolean checkExists(String key) {
        JobKey jobKey = createJobKey(key);
        try {
            return getScheduler().checkExists(jobKey);
        } catch (SchedulerException e) {
            log.warn(String.format("check job exists error: %s", jobKey), e);
            return false;
        }
    }

    public static boolean deleteJob(String key) {
        JobKey jobKey = createJobKey(key);
        try {
            return getScheduler().deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.warn(String.format("delete job error: %s", jobKey), e);
            return false;
        }
    }

    public static boolean pauseJob(String key) {
        JobKey jobKey = createJobKey(key);
        try {
            getScheduler().pauseJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            log.warn(String.format("pause job error: %s", jobKey), e);
            return false;
        }
    }

    public static boolean resumeJob(String key) {
        JobKey jobKey = createJobKey(key);
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
        return JobBuilder.newJob()
                .ofType(clazz)
                .withIdentity(createJobKey(key))
                .usingJobData(jobDataMap)
                // 调度器执行任务时，遇到'recovery'或者'fail-over'重新执行
                .requestRecovery()
                // 没有触发器也不删除该任务
                .storeDurably()
                .build();
    }

    public static CronTrigger createCronTrigger(String key, String cron) {
        return createCronTrigger(key, cron, new JobDataMap());
    }

    public static CronTrigger createCronTrigger(String key, String cron, JobDataMap jobDataMap) {
        return TriggerBuilder.newTrigger()
                .withIdentity(createTriggerKey(key))
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .usingJobData(jobDataMap)
                // 从现在开始执行
                .startNow()
                // .startAt()
                // .endAt()
                .withPriority(Trigger.DEFAULT_PRIORITY)
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
