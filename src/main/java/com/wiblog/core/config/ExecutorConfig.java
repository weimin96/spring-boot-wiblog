package com.wiblog.core.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * 线程池配置类
 *
 * @author pwm
 * @date 2019/11/19
 */
@Component
@Slf4j
public class ExecutorConfig {

    @Value("${executor.pool-size}")
    private int corePoolSize;
    @Value("${executor.max-pool-size}")
    private int maxPoolSize;
    @Value("${executor.queue-capacity}")
    private int queueCapacity;

    /**
     * 使用线程池管理
     */
    private final ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("thread-%d").daemon(true).build();

    @Bean(name = "taskExecutor")
    public ExecutorService taskExecutorService() {
        BlockingQueue<Runnable> queue = createQueue(this.queueCapacity);
        return new ThreadPoolExecutor(this.corePoolSize, this.maxPoolSize, 60L, TimeUnit.SECONDS,
                queue, threadFactory);
    }

    protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
        if (queueCapacity > 0) {
            return new LinkedBlockingQueue<>(queueCapacity);
        } else {
            return new SynchronousQueue<>();
        }
    }
}
