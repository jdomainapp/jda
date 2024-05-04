package jda.modules.tasltool.utils;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.tasltool.contracts.IData;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Templator {
    public static void useTemplate(File from, File container, Map<String, Object> data) {
        if (from.isFile()) {
            // rename file
            String name = renderText(from.getName(), data);
            // create file
            File to = new File(container, name);

            // render file
            String content = renderFile(from, data);
            try {
                FileUtils.writeStringToFile(to, content);
            } catch (IOException e) {
                throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_FILE, new Object[]{ from });
            }

            // TODO: if java file -> validate by parsing
        } else {
            for (File file : from.listFiles()) {
                File _container = container;

                if (file.isDirectory()) {
                    // rename
                    String name = renderText(file.getName(), data);
                    _container = new File(container, name);
                    _container.mkdirs();
                }

                useTemplate(file, _container, data);
            }
        }
    }

    public static String renderFile(File file, Map<String, Object> data) {
        String content = null;
        try {
            content = FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_FILE, new Object[]{ file });
        }

        return renderText(content, data);
    }

    public static String renderText(String text, Map<String, Object> data) {
        if (data == null) {
            return text;
        }

        text = renderTags(text, data);

        for (String key : data.keySet()) {
            Object value = data.get(key);
            if (value instanceof String) {
                try {
                    text = text.replaceAll("__" + key + "__", (String) value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // TODO: render not String type
            }
        }

        return text;
    }

    public static String renderTags(String text, Map<String, Object> _data) {
        Map<String, Object> data = new HashMap<>(_data);

        String[] tags = { "foreach" };

        for(String tag : tags) {

            String openingTag = "__" + tag;
            String closingTag = "__end" + tag;


            // TODO:---------------- more than 1 occurrence

            int begin = 0;
            while(begin >=0) {
                // find opening tag
                begin = text.indexOf(openingTag, begin);

                if (begin < 0) {
                    continue;
                }

                // find closing tag
                // TODO: handle nested tag
                int end = text.indexOf(closingTag);
                if (end < 0) {
                    throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, "missing closing tag: " + closingTag);
                }

                // render tag
                String result = renderTag(tag, text.substring(begin, end + closingTag.length()), data);

                text = text.substring(0, begin) + result + text.substring(end + closingTag.length());
            }
        }

        return text;
    }

    public static String renderTag(String tag, String text, Map<String, Object> _data) {
        switch(tag) {
            case "foreach":
                text = renderForeach(text, _data);
                break;
        }
        return text;
    }

    public static String renderForeach(String text, Map<String, Object> _data) {
        try {
            String params = text.substring(text.indexOf("(")+1, text.indexOf(")"));
            String[] temp = params.split(":");
            String items = temp[1].trim();
            String item = temp[0].trim();

            String content = text.substring(text.indexOf(")")+1, text.indexOf("__endforeach"));
            Map<String, Object> data = new HashMap<>(_data);

            List<IData> itemsData = (List<IData>) data.get(items);
            if (itemsData.isEmpty()) {
                return "";
            }

            for(IData _item : (List<IData>) data.get(items)) {
                Map<String, Object> itemData = _item.getData();
                for (String key: itemData.keySet()) {
                    data.put(item+"."+key, itemData.get(key));
                }
            }

            return renderText(content, data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, "Invalid infor for tag: __foreach");
        }
    }
}
