package com.furkan.banking.mapper;

import com.furkan.banking.dto.AccountDTO;
import com.furkan.banking.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(source = "userId", target = "user.id")
    Account toAccount(AccountDTO accountDTO);

    @Mapping(source = "user.id", target = "userId")
    AccountDTO toAccountDTO(Account account);
}
