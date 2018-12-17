package org.san.home.accounts.jpa;

import org.san.home.accounts.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * @author sanremo16
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
