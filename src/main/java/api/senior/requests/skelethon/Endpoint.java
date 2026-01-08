package api.senior.requests.skelethon;

import api.senior.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import api.senior.models.deposit.MakeDepositRequestModel;
import api.senior.models.profile.ChangeNameRequestModel;
import api.senior.models.profile.ChangeNameResponseModel;
import api.senior.models.profile.GetProfileResponseModel;
import api.senior.models.transfer.TransferMoneyRequestModel;
import api.senior.models.transfer.TransferMoneyResponseModel;

@Getter
@AllArgsConstructor
public enum Endpoint {
    ADMIN_USER(
            "/admin/users",
            CreateUserRequest.class,
            CreateUserResponse.class
    ),

    LOGIN(
            "/auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class
    ),

    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            AccountResponseModel.class
    ),

    UPDATE_PROFILE(
            "/customer/profile",
            ChangeNameRequestModel.class,
            ChangeNameResponseModel.class
    ),

    GET_PROFILE(
            "/customer/profile",
            BaseModel.class,
            GetProfileResponseModel.class
    ),

    CUSTOMER_ACCOUNTS(
            "/customer/accounts",
            BaseModel.class,
            AccountResponseModel.class
    ),

    DEPOSIT(
            "/accounts/deposit",
            MakeDepositRequestModel.class,
            AccountResponseModel.class
    ),
    TRANSFER(
            "/accounts/transfer",
            TransferMoneyRequestModel.class,
            TransferMoneyResponseModel.class
    );


    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}