package chainj.protocol.tx;

import chainj.protocol.bc.Hash;
import chainj.protocol.bc.TxData;
import chainj.protocol.bc.TxHashes;
import chainj.protocol.bc.VMContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbwdlihao on 06/02/2017.
 */
public class Transaction {

    public static TxHashes txHashes(TxData oldTx) {
        MapTxResult result = Map.mapTx(oldTx);
        EntryRef txID = result.headerID;
        Header header = result.header;
        java.util.Map<EntryRef, EntryInterface> entryMap = result.entryMap;

        TxHashes hashes = new TxHashes();
        hashes.setId(txID.hash());

        // OutputIDs
        Hash[] outputIDs = new Hash[header.getBody().results.length];
        for (int i = 0; i < header.getBody().results.length; i++) {
            EntryRef resultHash = header.getBody().results[i];
            EntryInterface entryInterface = entryMap.get(resultHash);
            if (entryInterface instanceof Output) {
                outputIDs[i] = resultHash.hash();
            } else {
                outputIDs[i] = Hash.emptyStringHash;
            }
        }
        hashes.setOutputIDs(outputIDs);

        Hash txRefDataHash;
        if (header.getBody().data.equals(new EntryRef())) {
            // no data entry
            txRefDataHash = Hash.emptyStringHash;
        } else {
            if (!entryMap.containsKey(header.getBody().data)) {
                throw new IllegalArgumentException("header refers to nonexistent data entry");
            }
            EntryInterface entryInterface = entryMap.get(header.getBody().data);
            if (!(entryInterface instanceof Data)) {
                throw new IllegalArgumentException(String.format("header refers to %s entry, should be data", entryInterface.getClass().getName()));
            }
            txRefDataHash = ((Data) entryInterface).getBody();
        }

        VMContext[] vmContexts = new VMContext[oldTx.getInputs().length];
        List<chainj.protocol.bc.Issuance> issuances = new ArrayList<>();
        entryMap.forEach((k, v) -> {
            if (v instanceof Nonce) {
                Nonce nonce = (Nonce)v;
                EntryRef trID = nonce.getBody().timeRange;
                if (!entryMap.containsKey(trID)) {
                    throw new IllegalArgumentException("nonce entry refers to nonexistent timerange entry");
                }
                EntryInterface entryInterface = entryMap.get(trID);
                if (!(entryInterface instanceof TimeRange)) {
                    throw new IllegalArgumentException(String.format("nonce entry refers to %s entry, should be timerange", entryInterface.getClass().getName()));
                }
                TimeRange tr = (TimeRange)entryInterface;
                chainj.protocol.bc.Issuance issuance = new chainj.protocol.bc.Issuance(k.hash(), tr.getBody().maxTimeMS);
                issuances.add(issuance);
            } else if (v instanceof Issuance) {
                Issuance issuance = (Issuance)v;
                VMContext vmContext = new VMContext(k.hash(), hashes.getId(), txRefDataHash);
                vmContext.setRefDataHash(issuance.getBody().data.hash());
                vmContext.setNonceID(issuance.getBody().anchor.hash());
                vmContexts[v.ordinal()] = vmContext;
            } else if (v instanceof Spend) {
                Spend spend = (Spend)v;
                VMContext vmContext = new VMContext(k.hash(), hashes.getId(), txRefDataHash);
                vmContext.setRefDataHash(spend.getBody().data.hash());
                vmContext.setOutputID(spend.getBody().spentOutput.hash());
                vmContexts[v.ordinal()] = vmContext;
            }
        });
        hashes.setIssuances(issuances.toArray(new chainj.protocol.bc.Issuance[0]));
        hashes.setVmContexts(vmContexts);
        return hashes;
    }
}
