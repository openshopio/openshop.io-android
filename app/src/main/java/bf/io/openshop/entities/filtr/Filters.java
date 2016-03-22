package bf.io.openshop.entities.filtr;

import java.util.List;


public class Filters {

    // Values parsed from API
    private List<FilterType> filters;

    public Filters() {
    }

    public Filters(List<FilterType> filters) {
        this.filters = filters;
    }

    public List<FilterType> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterType> filters) {
        this.filters = filters;
    }
}
