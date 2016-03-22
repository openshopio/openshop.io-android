package bf.io.openshop.entities.filtr;

public class FilterTypeRange extends FilterType {

    /**
     * Currently selected min value
     */
    private transient int selectedMin = -1;
    /**
     * Currently selected max value
     */
    private transient int selectedMax = -1;

    private int min;
    private int max;
    private String rangeTitle;

    public FilterTypeRange() {
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getRangeTitle() {
        return rangeTitle;
    }

    public void setRangeTitle(String rangeTitle) {
        this.rangeTitle = rangeTitle;
    }

    public int getSelectedMin() {
        return selectedMin;
    }

    public void setSelectedMin(int selectedMin) {
        this.selectedMin = selectedMin;
    }

    public int getSelectedMax() {
        return selectedMax;
    }

    public void setSelectedMax(int selectedMax) {
        this.selectedMax = selectedMax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FilterTypeRange that = (FilterTypeRange) o;

        if (selectedMin != that.selectedMin) return false;
        if (selectedMax != that.selectedMax) return false;
        if (min != that.min) return false;
        if (max != that.max) return false;
        return !(rangeTitle != null ? !rangeTitle.equals(that.rangeTitle) : that.rangeTitle != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + selectedMin;
        result = 31 * result + selectedMax;
        result = 31 * result + min;
        result = 31 * result + max;
        result = 31 * result + (rangeTitle != null ? rangeTitle.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FilterTypeRange{" +
                "selectedMin=" + selectedMin +
                ", selectedMax=" + selectedMax +
                ", min=" + min +
                ", max=" + max +
                ", rangeTitle='" + rangeTitle + '\'' +
                '}';
    }
}
