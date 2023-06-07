package com.xiuGEN.service.task;

import com.xiuGEN.context.PermissionContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 * @program: GEN_Multifunctional
 * @description: 定时任务
 * @author: xiuGEN
 * @create: 2023-05-28 13:56
 **/
@Component
public class TaskSchedulerUrl {
    private ThreadPoolTaskScheduler taskScheduler;

    public void startScheduledTasks() {
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(2);
        taskScheduler.initialize();
        // 添加定时任务每5分钟执行一次
        taskScheduler.schedule(this::runScheduledTask, new CronTrigger("0 0/5 * * * *")); // 每分钟执行一次
    }

    public synchronized void stopScheduledTasks() {
        taskScheduler.shutdown();
        taskScheduler=null;
    }

    private void runScheduledTask() {
        PermissionContext.getTempPaths().entrySet().removeIf(entry->{
            boolean pathExpired = PermissionContext.isPathExpired(entry.getKey());
            if (pathExpired) PermissionContext.removePermissionPaths(entry.getKey());
            return pathExpired;
        });
        if (taskScheduler !=null){
            if (PermissionContext.getTempPaths().isEmpty()) stopScheduledTasks();
        }
    }

    public ThreadPoolTaskScheduler getTaskScheduler() {
        return taskScheduler;
    }
}
