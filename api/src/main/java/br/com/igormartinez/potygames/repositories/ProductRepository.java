package br.com.igormartinez.potygames.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.igormartinez.potygames.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {}
