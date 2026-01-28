package com.microloan.microloan_issuance.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InfoClientResponseModel {
    private String acc_id;
    @JsonProperty("vip-client")
    private boolean vip_client;
    private boolean blocked;
    private String inn;
    private List<DebtResponseModel> debt;
}
