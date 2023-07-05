package br.com.igormartinez.potygames.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.igormartinez.potygames.models.YugiohCardCategory;

@Repository
public interface YugiohCardCategoryRepository extends JpaRepository<YugiohCardCategory, Long> {}
