package ir.alimojahed.general.elasticwrapper.domain.model.common;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CargoState {
    PROCESS, WAREHOUSE, LOCAL_DELIVERY, GLOBAL_DELIVERY, SHOP, CUSTOMER
}
