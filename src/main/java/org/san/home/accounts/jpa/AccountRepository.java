package org.san.home.accounts.jpa;

import org.san.home.accounts.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Collection;
import java.util.Optional;


/**
 * @author sanremo16
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.num= :num")
    <S extends Account> Optional<S> findOneByNumForUpdate(@Param("num") String num);

    <S extends Account> Collection<S> findByNum(@Param("num") String num);

    void deleteByNum(@Param("num") String num);
}
