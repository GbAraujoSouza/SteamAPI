package bd.trabalho.steamapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SteamApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SteamApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> System.out.println("CommandLine Runner started");
    }

}
