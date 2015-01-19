package com.nantaphop.pantipfanapp.response;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Nantaphop
 * Date: 8/17/13
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyPage implements Serializable {

    int page;
    int max_page;
    long first_id;
    long last_id;
    String status;

    ArrayList<Topic> result;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getMax_page() {
        return max_page;
    }

    public void setMax_page(int max_page) {
        this.max_page = max_page;
    }

    public long getFirst_id() {
        return first_id;
    }

    public void setFirst_id(long first_id) {
        this.first_id = first_id;
    }

    public long getLast_id() {
        return last_id;
    }

    public void setLast_id(long last_id) {
        this.last_id = last_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Topic> getResult() {
        return result;
    }

    public void setResult(ArrayList<Topic> result) {
        this.result = result;
    }
}
