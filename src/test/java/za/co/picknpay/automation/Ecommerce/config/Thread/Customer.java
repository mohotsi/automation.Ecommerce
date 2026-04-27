package za.co.picknpay.automation.Ecommerce.config.Thread;

import lombok.*;


import java.time.LocalDateTime;
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Customer {
    private String name;
    private String lastName;
    private String password;

    private String email;
    private String originalEmail;

    private String emailSecretKey;
    private boolean isRegistered;
    private LocalDateTime actionTimeStamp;
    public Customer(String email,String emailSecretKey) {
        this.email = email;
        this.originalEmail=email;
        this.emailSecretKey=emailSecretKey;

    }



}
