package com.caloryhive.business.finance.repository;

import com.caloryhive.business.finance.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    List<BankAccount> findByBusinessIdOrderByIsDefaultDescCreatedAtDesc(UUID businessId);
    Optional<BankAccount> findByIdAndBusinessId(UUID id, UUID businessId);
}
