package com.gaming.community.flexster.model;

import java.util.Comparator;

public class SortByrankModel
{
    String user_id,rank;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public static class RatingComparator implements Comparator<SortByrankModel> {
        @Override
        public int compare(SortByrankModel obj1, SortByrankModel obj2) {
            int rank1 = Integer.parseInt(obj1.rank);
            int rank2 = Integer.parseInt(obj2.rank);

            return (rank1 < rank2) ? -1 : (rank1 > rank2) ? 1 : 0;
        }

    }
}



