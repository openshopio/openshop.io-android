package bf.io.openshop.entities.filtr;


import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DeserializerFilters implements JsonDeserializer<Filters> {

    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_TYPE = "type";
    private static final String TAG_LABEL = "label";
    private static final String TAG_VALUES = "values";

    public static final String FILTER_TYPE_COLOR = "color";
    public static final String FILTER_TYPE_SELECT = "select";
    public static final String FILTER_TYPE_RANGE = "range";


    @Override
    public Filters deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Check if whole object is available
        if (json.isJsonArray()) {
            final Filters filters = new Filters();
            List<FilterType> filterList = new ArrayList<>();

            JsonArray jArray = (JsonArray) json;
            for (int i = 0; i < jArray.size(); i++) {
                JsonElement jElement = jArray.get(i);
                if (jElement.isJsonObject()) {
                    JsonObject jObject = (JsonObject) jElement;
                    if (jObject.has(TAG_TYPE)) {
                        String type = jObject.get(TAG_TYPE).getAsString();
                        if (FILTER_TYPE_COLOR.equals(type)) {
                            final FilterTypeColor filterTypeColor = new FilterTypeColor();
                            filterTypeColor.setType(FILTER_TYPE_COLOR);
                            parseGeneralFields(jObject, filterTypeColor);
                            filterTypeColor.setValues(parseTypeValues(FilterValueColor.class, jObject, context));
                            if (filterTypeColor.getValues() != null) {
                                filterList.add(filterTypeColor);
                            }
                        } else if (FILTER_TYPE_SELECT.equals(type)) {
                            final FilterTypeSelect filterTypeSelect = new FilterTypeSelect();
                            filterTypeSelect.setType(FILTER_TYPE_SELECT);
                            parseGeneralFields(jObject, filterTypeSelect);
                            filterTypeSelect.setValues(parseTypeValues(FilterValueSelect.class, jObject, context));
                            if (filterTypeSelect.getValues() != null) {
                                filterList.add(filterTypeSelect);
                            }
                        } else if (FILTER_TYPE_RANGE.equals(type)) {
                            final FilterTypeRange filterTypeRange = new FilterTypeRange();
                            filterTypeRange.setType(FILTER_TYPE_RANGE);
                            parseGeneralFields(jObject, filterTypeRange);
                            if (jObject.has(TAG_VALUES)) {
                                JsonArray rangeValues = jObject.get(TAG_VALUES).getAsJsonArray();
                                if (rangeValues != null && rangeValues.size() == 3) {
                                    filterTypeRange.setMin(rangeValues.get(0).getAsInt());
                                    filterTypeRange.setMax(rangeValues.get(1).getAsInt());
                                    filterTypeRange.setRangeTitle(rangeValues.get(2).getAsString());
                                }
                            }
                            filterList.add(filterTypeRange);
                        }
                    }
                }
            }
            if (!filterList.isEmpty())
                filters.setFilters(filterList);
            return filters;
        }
        throw new JsonParseException("Unexpected JSON type: " + json.getClass().getSimpleName());
    }

    /**
     * Parse ID and NAME for a type of filter.
     *
     * @param jObject
     * @param filterType
     */
    private void parseGeneralFields(JsonObject jObject, FilterType filterType) {
        if (jObject.has(TAG_ID))
            filterType.setId(jObject.get(TAG_ID).getAsLong());
        if (jObject.has(TAG_NAME))
            filterType.setName(jObject.get(TAG_NAME).getAsString());
        if (jObject.has(TAG_LABEL))
            filterType.setLabel(jObject.get(TAG_LABEL).getAsString());
    }


    private <T extends Object> List<T> parseTypeValues(Class<T> type, JsonObject jObject, JsonDeserializationContext context) {
        List<T> values = null;
        if (jObject.has(TAG_VALUES)) {
            JsonArray valuesArray = jObject.get(TAG_VALUES).getAsJsonArray();
            values = new ArrayList<>();

            for (int i = 0; i < valuesArray.size(); i++) {
                values.add(type.cast(context.deserialize(valuesArray.get(i), type)));
            }
        }
        if (values == null || values.isEmpty())
            return null;
        else
            return values;
    }
}