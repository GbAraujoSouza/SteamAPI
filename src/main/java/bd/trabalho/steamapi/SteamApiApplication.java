package bd.trabalho.steamapi;

import bd.trabalho.steamapi.services.SteamSyncService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SteamApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SteamApiApplication.class, args);
    }

    /**
     * This bean creates a CommandLineRunner, which is a simple Spring Boot component
     * that runs code once the application has started.
     * Here, it triggers our synchronization service to fetch data on startup.
     * @param steamSyncService The service to be executed.
     * @return A configured CommandLineRunner instance.
     */
    @Bean
    public CommandLineRunner syncOnStartup(SteamSyncService steamSyncService) {
        return args -> {
            System.out.println("EXECUTING: Syncing Steam data on application startup...");
            steamSyncService.syncAllDataForUser();
            System.out.println("SUCCESS: Initial data sync complete.");
        };
    }



}
