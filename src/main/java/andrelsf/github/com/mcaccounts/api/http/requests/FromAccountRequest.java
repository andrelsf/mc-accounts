package andrelsf.github.com.mcaccounts.api.http.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FromAccountRequest(
    @NotBlank String fullName,
    @NotBlank String cpf,
    @NotNull @Positive Integer agency,
    @NotNull @Positive Integer accountNumber
) {

}
