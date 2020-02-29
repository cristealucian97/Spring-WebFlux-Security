package com.demo.reactive.infrastructure;

import com.demo.reactive.domain.TokenEntityDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<TokenEntityDto, String> {

    TokenEntityDto findByValue(String value);
}
