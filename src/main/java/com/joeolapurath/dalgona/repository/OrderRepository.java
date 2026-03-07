package com.joeolapurath.dalgona.repository;

import com.joeolapurath.dalgona.model.Account;
import com.joeolapurath.dalgona.model.Order;
import com.joeolapurath.dalgona.model.Umbrella;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByAccountOrderByRentedAtDesc(Account account);

    List<Order> findByAccountAndReturnedAtIsNullOrderByRentedAtDesc(Account account);

    Optional<Order> findByUmbrellaAndReturnedAtIsNull(Umbrella umbrella);

    boolean existsByAccountAndReturnedAtIsNull(Account account);
}
