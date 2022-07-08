package jda.modules.mosarfrontend.common.utils;

import jda.modules.mosarfrontend.common.factory.ParamsFactory;
import jda.modules.mosarfrontend.common.factory.Slot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MethodUtils {
    public static <T> T execute(Object executor, Method method, Class<T> returnType) {
        Object[] params = ParamsFactory.getInstance().getParamsForMethod(method);
        if (method.getParameters().length == params.length) {
            try {
                return returnType.cast(method.invoke(executor, params));
            } catch (IllegalAccessException | InvocationTargetException e) {
                System.out.println("Error when trigger " + method.getName() + "()");
                e.printStackTrace();
                return null;
            } catch (IllegalArgumentException e) {
                System.out.println("Error when trigger " + method.getName() + "()");
                System.out.println("Wrong argument type!");
                System.out.println("Method arguments: " + Arrays.stream(method.getParameters()).map(p -> String.format("(%s:%s),", p.getName(), p.getType().getSimpleName())).collect(Collectors.joining()));
                System.out.println("Try to pass arguments: (" + Arrays.stream(params).map(p -> String.format("%s ,", p.getClass().getSimpleName())).collect(Collectors.joining()) + ")");
                e.printStackTrace();
                return null;
            }
        } else return null;
    }

    public static Slot[][] toLoopData(ArrayList<ArrayList<Slot>> result) {
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
