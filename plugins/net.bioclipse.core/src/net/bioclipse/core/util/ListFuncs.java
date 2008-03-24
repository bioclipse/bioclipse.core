package net.bioclipse.core.util;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.core.util.Function;
import net.bioclipse.core.util.Predicate;

//TODO tests!

/**
 * ListFuncs:
 *   Utility functions for working with lists. Provides map, filter, etc.
 *   
 * @author rklancer
 *
 */
public class ListFuncs {

    /** Returns a list produced by applying the supplied Function to each
     *  element of the input list in order.
     * 
     * @param in
     *            the List of elements to apply the function to
     * @param f
     *            the Function to apply to each element of in
     *            
     * @return    List of the same size as in, containing result of evaluating 
     *            f.eval(x) for each element x of in.
     */
    public static <S, T> List<S> map(List<T> in, Function<S, T> f) {
        List<S> out = new ArrayList<S>(in.size());
        for (T x : in)
            out.add(f.eval(x));
        return out;
    }
    
    
    /** Returns a list produced by copying from the input list, in order, only
     *  those elements for which the supplied Predicate is true.
     * 
     * @param in
     *            the List of elements to apply the function to
     * @param p
     *            the Predicate to apply to each element of in
     *            
     * @return    List of those elements x of in for which p.eval(x) is true
     */
    public static <T> List<T> filter(List<T> in, Predicate<T> p) {
        List<T> out = new ArrayList<T>();
        for (T x : in)
            if (p.eval(x)) 
                out.add(x);
        return out;
    }
}
