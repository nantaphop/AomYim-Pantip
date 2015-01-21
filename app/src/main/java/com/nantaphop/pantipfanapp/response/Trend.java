package com.nantaphop.pantipfanapp.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by nantaphop on 21-Jan-15.
 */
public class Trend implements Serializable{
    Object related;
    List<Topic> trend;

    public Object getRelated() {
        return related;
    }

    public void setRelated(Object related) {
        this.related = related;
    }

    public List<Topic> getTrend() {
        return trend;
    }

    public void setTrend(List<Topic> trend) {
        this.trend = trend;
    }
}
