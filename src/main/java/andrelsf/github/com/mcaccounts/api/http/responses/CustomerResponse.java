package andrelsf.github.com.mcaccounts.api.http.responses;

public record CustomerResponse(
    String customerId,
    String fullName,
    String cpf
) {}
