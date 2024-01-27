package jda.modules.tasltool.contracts;

import jda.modules.tasltool.utils.DataBinder;

import java.util.HashMap;
import java.util.Map;

public interface IData {
    default Map<String, Object> getData() {
        Map<String, Object> attributeValues = DataBinder.getAttributeValues(this);

        // inner IData attributes
        Map<String, Object> temp = new HashMap<>();
        for (String attribute : attributeValues.keySet()) {
            Object attributeValue = attributeValues.get(attribute);

            if (attributeValue instanceof IData) {
                IData innerValues = (IData) attributeValue;
                Map<String, Object> innerData = innerValues.getData();

                // add prefix
                for (String key : innerData.keySet()) {
                    temp.put(attribute + "." + key, innerData.get(key));
                }
            }
        }

        attributeValues.putAll(temp);

        // TODO: class info

        return attributeValues;
    }
}
