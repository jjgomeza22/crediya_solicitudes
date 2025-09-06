package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.SendLoanApplicationDto;
import co.com.crediya.model.loanapplication.LoanApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stateId", constant = "4")
    LoanApplication toModel(SendLoanApplicationDto dto);
}
