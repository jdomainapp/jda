package jda.modules.tasltool.contracts;

import jda.modules.tasltool.utils.DataBinder;
import jda.modules.tasltool.utils.Templator;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface IGenerator extends IData {
    default String getOutputPath() {
        Object attributeValue = getData().get("outputPath");
        return attributeValue != null ? attributeValue.toString() : "";
    }

    default String getTemplate() {
        return "templates/" + getClass().getSimpleName();
    }


    default void generate() {
        generate(new HashMap<>());
    }

    default void generate(Map<String, Object> data) {
        String template = getTemplate();
        URL res = getClass().getClassLoader().getResource(template);

        if (res != null) {
            File file = new File(res.getFile());

            data.putAll(getData());
            Templator.useTemplate(file, new File(getOutputPath()), data);
        }

        Map<String, Object> _data = new HashMap<>();
        for (String key : data.keySet()) {
            _data.put(getClass().getSimpleName()+"."+key, data.get(key));
        }

        for (Field field : DataBinder.getAllFieldsIncludeSuperClass(getClass())) {
            try {
                field.setAccessible(true);
                if (IGenerator.class.isAssignableFrom(field.getType())) {

                    IGenerator generator = (IGenerator) field.get(this);
                    if (generator != null) {
                        generator.generate(_data);
                    }
                } else {
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        Collection c = (Collection) field.get(this);

                        for (Object o : c) {
                            if (o instanceof IGenerator) {
                                IGenerator generator = (IGenerator) o;
                                generator.generate(_data);
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}