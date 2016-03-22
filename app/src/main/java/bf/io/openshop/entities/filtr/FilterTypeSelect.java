package bf.io.openshop.entities.filtr;

import java.util.List;

public class FilterTypeSelect extends FilterType {

    /**
     * Currently selected value
     */
    private transient FilterValueSelect selectedValue = null;

    private List<FilterValueSelect> values;

    public FilterTypeSelect() {
    }

    public FilterValueSelect getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(FilterValueSelect selectedValue) {
        this.selectedValue = selectedValue;
    }

    public List<FilterValueSelect> getValues() {
        return values;
    }

    public void setValues(List<FilterValueSelect> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FilterTypeSelect that = (FilterTypeSelect) o;

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
        return super.toString() + ".FilterTypeSelect{" +
                "selectedValue=" + selectedValue +
                ", values= ..." +
                '}';
    }

}
