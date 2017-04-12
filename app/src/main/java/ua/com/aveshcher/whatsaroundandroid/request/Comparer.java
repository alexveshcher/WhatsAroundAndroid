package ua.com.aveshcher.whatsaroundandroid.request;


import ua.com.aveshcher.whatsaroundandroid.dto.Place;

import java.util.HashSet;
import java.util.Set;

public class Comparer {

    public static Set<Place> diffPlaces(Set<Place> oldPlaces, Set<Place> newPlaces) {
        Set<Place> result = new HashSet<>();
        int countOld = oldPlaces.size();
        int countNew = newPlaces.size();
        if ((countOld + countNew) > 1) {
            for (Place p : newPlaces) {
                if (!oldPlaces.contains(p)) {
                    result.add(p);
                }
            }
        }
        return result;
    }
}
