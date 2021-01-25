package cdm.event.common;

import cdm.event.common.functions.FilterSecurityTransfers;
import cdm.observable.asset.AssetIdentifier;
import cdm.observable.asset.QuantityNotation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FilterSecurityTransfersImpl extends FilterSecurityTransfers {
    @Override
    protected Transfers.TransfersBuilder doEvaluate(List<Transfer> transfers) {
        List<Transfer> cashTransfers = transfers.stream().filter(this::hasSecurity).collect(Collectors.toList());
        return !cashTransfers.isEmpty() ? Transfers.builder().addTransfers(cashTransfers) : null;
    }

    private boolean hasSecurity(Transfer transfer) {
        return Optional.ofNullable(transfer).map(Transfer::getQuantity).map(QuantityNotation::getAssetIdentifier)
                .map(AssetIdentifier::getProductIdentifier).isPresent();
    }
}
