package com.sdsmdg.harjot.AZMusic.utilities.comparators;

import com.sdsmdg.harjot.AZMusic.models.Artist;

import java.util.Comparator;

/**
 * Created by Harjot on 18-Jan-17.
 */

public class ArtistComparator implements Comparator<Artist> {

    @Override
    public int compare(Artist lhs, Artist rhs) {
        return lhs.getName().toString().compareTo(rhs.getName().toString());
    }
}
