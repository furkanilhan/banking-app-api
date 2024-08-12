package com.furkan.banking.mapper;

import com.furkan.banking.dto.TransactionDTO;
import com.furkan.banking.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface TransactionMapper {

    @Mapping(source = "from.id", target = "fromAccountId")
    @Mapping(source = "to.id", target = "toAccountId")
    @Mapping(source = "from.number", target = "fromAccountNumber")
    @Mapping(source = "to.number", target = "toAccountNumber")
    TransactionDTO toTransactionDTO(Transaction transaction);
}
