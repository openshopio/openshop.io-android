package bf.io.openshop.entities.filtr;

import java.util.List;

public class FilterTypeColor extends FilterType {

    /**
     * Currently selected value
     */
    private transient FilterValueColor selectedValue = null;

    private List<FilterValueColor> values;

    public FilterTypeColor() {
    }

    public FilterValueColor getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(FilterValueColor selectedValue) {
        this.selectedValue = selectedValue;
    }

    public List<FilterValueColor> getValues() {
        return values;
    }

    public void setValues(List<FilterValueColor> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FilterTypeColor that = (FilterTypeColor) o;

        if (selectedValue != null ? !selectedValue.equals(that.selectedValue) : that.selectedValue != null)
            return false;
        return !(values != null ? !values.equals(that.values) : that.values != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (selectedValue != null ? selectedValue.hashCode() : 0);
        result = 31 * result + (values != null ? values.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + ". FilterTypeColor{" +
                "selectedValue=" + selectedValue +
                ", values= ..." +
                '}';
    }

}
