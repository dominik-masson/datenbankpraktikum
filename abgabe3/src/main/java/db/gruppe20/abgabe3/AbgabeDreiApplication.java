package db.gruppe20.abgabe3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude =  {DataSourceAutoConfiguration.class })
public class AbgabeDreiApplication {

    public static void main(String[] args) {

        SpringApplication.run(AbgabeDreiApplication.class, args);

         //UserShell userShell = initializeFrontend(initializeBackend());

    }

}