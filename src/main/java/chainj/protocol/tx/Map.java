package chainj.protocol.tx;

import chainj.protocol.bc.*;
import chainj.protocol.bc.txinput.IssuanceInput;
import chainj.protocol.bc.txinput.SpendInput;
import chainj.protocol.vm.OP;
import chainj.protocol.vmutil.Builder;
import chainj.protocol.vmutil.Script;

import java.util.HashMap;

/**
 * Created by sbwdlihao on 05/02/2017.
 */
class Map {

    static MapTxResult mapTx(TxData tx) {
        EntryRef refDataID = new EntryRef();
        java.util.Map<EntryRef, EntryInterface> entryMap = new HashMap<>();
        if (tx.getReferenceData().length > 0) {
            refDataID = addEntry(new Data(tx.getReferenceData()), entryMap).id;
        }

        // Loop twice over tx.Inputs, once for spends and once for
        // issuances.  Do spends first so the entry ID of the first spend is
        // available in case an issuance needs it for its anchor.

        EntryRef firstSpendID = null;
        ValueSource[] muxSources = new ValueSource[tx.getInputs().length];
        for (int i = 0; i < tx.getInputs().length; i++) {
            TxInput inp = tx.getInputs()[i];
            if (inp instanceof SpendInput) {
                SpendInput oldSp = (SpendInput)inp;
                EntryRef inpRefDataID = new EntryRef();
                if (oldSp.getReferenceData().length > 0) {
                    inpRefDataID = addEntry(new Data(oldSp.getReferenceData()), entryMap).id;
                }
                EntryRef spID = addEntry(new Spend(new EntryRef(oldSp.spentOutputID().getHash()), inpRefDataID, i), entryMap).id;
                muxSources[i] = new ValueSource(spID, new AssetAmount(oldSp.assetAmount()));
                if (firstSpendID == null) {
                    firstSpendID = spID;
                }
            }
        }

        for (int i = 0; i < tx.getInputs().length; i++) {
            TxInput inp = tx.getInputs()[i];
            if (inp instanceof IssuanceInput) {
                IssuanceInput oldIss = (IssuanceInput)inp;
                EntryRef inpRefDataID = new EntryRef();
                if (oldIss.getReferenceData().length > 0) {
                    inpRefDataID = addEntry(new Data(oldIss.getReferenceData()), entryMap).id;
                }
                // Note: asset definitions, initial block ids, and issuance
                // programs are omitted here because they do not contribute to
                // the body hash of an issuance.
                EntryRef nonceHash;
                if (oldIss.getNonce().length == 0) {
                    if (firstSpendID == null) {
                        throw new IllegalArgumentException("nonce-less issuance in transaction with no spends");
                    }
                    nonceHash = firstSpendID;
                } else {
                    EntryRef trID = addEntry(new TimeRange(tx.getMinTime(), tx.getMaxTime()), entryMap).id;
                    AssetID assetID = oldIss.assertID();
                    Builder b = new Builder();
                    b.addData(oldIss.getNonce()).addOP(OP.OP_DROP).
                            addOP(OP.OP_ASSET).addData(assetID.getValue()).addOP(OP.OP_EQUAL);
                    nonceHash = addEntry(new Nonce(new Program(1, b.getProgram()), trID), entryMap).id;
                }

                AssetAmount val = new AssetAmount(inp.assetAmount());
                EntryRef issID = addEntry(new Issuance(nonceHash, val, inpRefDataID, i), entryMap).id;
                muxSources[i] = new ValueSource(issID, val);
            }
        }

        EntryRef muxID = addEntry(new Mux(muxSources), entryMap).id;
        EntryRef[] results = new EntryRef[tx.getOutputs().length];
        for (int i = 0; i < tx.getOutputs().length; i++) {
            TxOutput out = tx.getOutputs()[i];
            ValueSource s = new ValueSource(muxID, new AssetAmount(out.getAssetAmount()), i);

            EntryRef outRefDataID = new EntryRef();
            if (out.getReferenceData().length > 0) {
                outRefDataID = addEntry(new Data(out.getReferenceData()), entryMap).id;
            }
            EntryRef resultID;
            if (Script.isUnSpendable(out.getControlProgram())) {
                // retirement
                resultID = addEntry(new Retirement(s, outRefDataID, i), entryMap).id;
            } else {
                Program program = new Program(out.getVmVersion(), out.getControlProgram());
                resultID = addEntry(new Output(s, program, outRefDataID, i), entryMap).id;
            }
            results[i] = resultID;
        }

        AddEntryResult addEntryResult = addEntry(new Header(tx.getVersion(), results, refDataID, tx.getMinTime(), tx.getMaxTime()), entryMap);
        return new MapTxResult(addEntryResult.id, (Header) addEntryResult.e, entryMap);
    }

    private static AddEntryResult addEntry(EntryInterface e, java.util.Map<EntryRef, EntryInterface> entryMap) {
        EntryRef id = Entry.entryID(e);
        entryMap.put(id, e);
        return new AddEntryResult(id, e);
    }
}

class AddEntryResult {
    EntryRef id;
    EntryInterface e;

    AddEntryResult(EntryRef id, EntryInterface e) {
        this.id = id;
        this.e = e;
    }
}

class MapTxResult {
    EntryRef headerID;
    Header header;
    java.util.Map<EntryRef, EntryInterface> entryMap;

    MapTxResult(EntryRef headerID, Header header, java.util.Map<EntryRef, EntryInterface> entryMap) {
        this.headerID = headerID;
        this.header = header;
        this.entryMap = entryMap;
    }
}
