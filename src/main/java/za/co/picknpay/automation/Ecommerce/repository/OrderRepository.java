package za.co.picknpay.automation.Ecommerce.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.picknpay.automation.Ecommerce.models.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    // You can add custom finders here later if needed
}