package bf.io.openshop.entities;

import java.util.List;

public class BannersResponse {

    private Metadata metadata;
    private List<Banner> records;

    public BannersResponse() {
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<Banner> getRecords() {
        return records;
    }

    public void setRecords(List<Banner> records) {
        this.records = records;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BannersResponse)) return false;

        BannersResponse that = (BannersResponse) o;

        if (getMetadata() != null ? !getMetadata().equals(that.getMetadata()) : that.getMetadata() != null) return false;
        return !(getRecords() != null ? !getRecords().equals(that.getRecords()) : that.getRecords() != null);

    }

    @Override
    public int hashCode() {
        int result = getMetadata() != null ? getMetadata().hashCode() : 0;
        result = 31 * result + (getRecords() != null ? getRecords().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BannersResponse{" +
                "metadata=" + metadata +
                ", records=" + records +
                '}';
    }
}