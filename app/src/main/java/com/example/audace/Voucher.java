package com.example.audace;

public class Voucher {
    String id;
    String name;
    String start;
    String end;
    String code;

    public Voucher(String id, String name, String start, String end, String code, String saleoff) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = end;
        this.code = code;
        this.saleoff = saleoff;
    }

    public String getSaleoff() {
        return saleoff;
    }

    public void setSaleoff(String saleoff) {
        this.saleoff = saleoff;
    }

    String saleoff;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }





}
