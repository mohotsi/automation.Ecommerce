package za.co.monateRetail.automation.Ecommerce.config.Thread;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "customer.pool")
@Data
public class CustomerProperties {
    private List<Customer> customers= new ArrayList<>();
}
