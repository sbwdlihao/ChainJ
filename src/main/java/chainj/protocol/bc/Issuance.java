package chainj.protocol.bc;

import java.util.Objects;

/**
 * Created by sbwdlihao on 06/02/2017.
 */
public class Issuance {
    private Hash id = new Hash();

    private long expirationMS;

    Hash getId() {
        return id;
    }

    void setId(Hash id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    long getExpirationMS() {
        return expirationMS;
    }

    void setExpirationMS(long expirationMS) {
        this.expirationMS = expirationMS;
    }

    public Issuance() {
    }

    public Issuance(Hash id, long expirationMS) {
        this.id = id;
        this.expirationMS = expirationMS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Issuance issuance = (Issuance) o;

        if (expirationMS != issuance.expirationMS) return false;
        return id != null ? id.equals(issuance.id) : issuance.id == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (expirationMS ^ (expirationMS >>> 32));
        return result;
    }
}
