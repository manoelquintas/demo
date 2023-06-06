package com.example.demo;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Slf4j
public class AsyncTeste {

    private ApplicationContext context;
    @Autowired
    @Qualifier("fileExecutor")
    TaskExecutor fileExecutor;
    @Async("fileExecutor")
    public CompletableFuture testar(String listaString) {

        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) context.getBean("fileExecutor");
        System.out.println(taskExecutor.getThreadNamePrefix());
        String [] a={"a"};
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(listaString.equalsIgnoreCase("d"))
                System.out.println(a[2]);

            log.info(listaString);
            return true;
        });
        }
}
