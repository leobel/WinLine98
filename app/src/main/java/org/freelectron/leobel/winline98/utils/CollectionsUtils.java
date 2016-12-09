package org.freelectron.leobel.winline98.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by leobel on 10/19/16.
 */
public class CollectionsUtils {

    public static <T> List<T> filter(List<T> target, Predicate<T> predicate){
        List<T> result = new ArrayList<>();
        for(T t : target){
            if(predicate.apply(t))
                result.add(t);
        }

        return result;
    }

    public static <T, K> List<K> map(Collection<T> target, Action<T, K> action){
        List<K> result = new ArrayList<>();
        for(T t : target){
            result.add(action.apply(t));
        }
        return result;
    }



    public interface Action<T, K>{
        K apply(T t);
    }

    public interface Predicate<T> {
        boolean apply(T t);
    }
}
