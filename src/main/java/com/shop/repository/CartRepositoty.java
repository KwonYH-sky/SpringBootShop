package com.shop.repository;

import com.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CartRepositoty extends JpaRepository<Cart, Long> {

}
