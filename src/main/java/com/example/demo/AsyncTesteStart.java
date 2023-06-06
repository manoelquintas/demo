package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.build.EntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@Service
@AllArgsConstructor
@Slf4j
public class AsyncTesteStart {

    private AsyncTeste asyncTeste;

   // @Bean
    public void testar() {

        long startTimeOne = System.currentTimeMillis();
        List<CompletableFuture> listCompletableFuture = new ArrayList<>();
        List<String> lista = new ArrayList<>();
        lista.add("A");
        lista.add("b");
        lista.add("c");
        lista.add("d");
        lista.add("e");
        try {
        lista.forEach(listaString -> {listCompletableFuture.add(asyncTeste.testar( listaString).handleAsync((res, ex) -> {
                if (ex == null) {
                    log.info("Sucesso no Processo : ");
                    return true;
                } else {
                    log.info("erro no Processo : " );
                    return false;
                }
            }).toCompletableFuture());
        });
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        log.info("Waiting for join");


        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(listCompletableFuture.toArray(new CompletableFuture[listCompletableFuture.size()]));

       // combinedFuture.join();

        try {
           log.info(String.valueOf(combinedFuture.isDone()));
            combinedFuture.get(20000, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            log.info("erro no get");
            log.error(e.getMessage());
            e.printStackTrace();
        }
        //log.info(String.valueOf(combinedFuture.isDone()));
        log.info("Finalizado");
        long estimatedTimeOne = System.currentTimeMillis() - startTimeOne;
        System.out.println("Total time async: " + estimatedTimeOne);

    }

}
