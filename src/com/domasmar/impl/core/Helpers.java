package com.domasmar.impl.core;

import java.util.ArrayList;

/**
 * Created by domas on 14.11.5.
 */
public class Helpers {

    static public boolean codeInList(ArrayList<Integer> list, int code) {
        for (Integer item : list) {
            if (item.intValue() == code) {
                return true;
            }
        }
        return false;
    }
}
