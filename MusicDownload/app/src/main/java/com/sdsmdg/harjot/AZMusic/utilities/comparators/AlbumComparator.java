package com.sdsmdg.harjot.AZMusic.utilities.comparators;

import com.sdsmdg.harjot.AZMusic.models.Album;

import java.util.Comparator;

/**
 * Created by Harjot on 18-Jan-17.
 */

public class AlbumComparator implements Comparator<Album> {

    @Override
    public int compare(Album lhs, Album rhs) {
        return lhs.getName().toString().compareTo(rhs.getName().toString());
    }
}
