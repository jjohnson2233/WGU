package com.example.v_jarj.wgu;

import android.content.ContentValues;
import android.net.Uri;
import android.content.Context;

import java.util.Date;

/**
 * Created by jaredjohnson on 3/11/18.
 */

public class Term {
    private int id;
    private String title;
    private Date startDate;
    private Date endDate;

    public Term(String title, Date startDate, Date endDate) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
