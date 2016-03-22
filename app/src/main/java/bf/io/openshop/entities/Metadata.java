package bf.io.openshop.entities;

import com.google.gson.annotations.SerializedName;

import bf.io.openshop.entities.filtr.Filters;

public class Metadata {

    private Links links;
    private Filters filters;
    private String sorting;

    @SerializedName("records_count")
    private int recordsCount;

    public Metadata() {

    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Filters getFilters() {
        return filters;
    }

    public void setFilters(Filters filters) {
        this.filters = filters;
    }

    public String getSorting() {
        return sorting;
    }

    public void setSorting(String sorting) {
        this.sorting = sorting;
    }

    public int getRecordsCount() {
        return recordsCount;
    }

    public void setRecordsCount(int recordsCount) {
        this.recordsCount = recordsCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metadata metadata = (Metadata) o;

        if (recordsCount != metadata.recordsCount) return false;
        if (links != null ? !links.equals(metadata.links) : metadata.links != null) return false;
        if (filters != null ? !filters.equals(metadata.filters) : metadata.filters != null)
            return false;
        return !(sorting != null ? !sorting.equals(metadata.sorting) : metadata.sorting != null);

    }

    @Override
    public int hashCode() {
        int result = links != null ? links.hashCode() : 0;
        result = 31 * result + (filters != null ? filters.hashCode() : 0);
        result = 31 * result + (sorting != null ? sorting.hashCode() : 0);
        result = 31 * result + recordsCount;
        return result;
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "links=" + links +
                ", filters=" + filters +
                ", sorting='" + sorting + '\'' +
                ", recordsCount=" + recordsCount +
                '}';
    }
}
