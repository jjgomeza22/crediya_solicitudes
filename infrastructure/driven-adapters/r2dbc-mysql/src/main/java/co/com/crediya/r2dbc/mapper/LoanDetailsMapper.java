package co.com.crediya.r2dbc.mapper;

import co.com.crediya.model.loandetails.LoanDetails;
import co.com.crediya.r2dbc.repository.dto.LoanDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanDetailsMapper {


    @Mapping(target = "id", source = "loanId")
    @Mapping(target = "amount", source = "loanAmount")
    @Mapping(target = "timeLimit", source = "timeToLimit")
    LoanDetails toModel(LoanDetailsDto dto);
}
