package cn.christian.server.dao;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/10.
 */
public class Record implements Serializable {

    private int id;
    private String code;
    private String datahash;
    private String data;

    public long getCreatedate() {
        return createdate;
    }

    public void setCreatedate(long createdate) {
        this.createdate = createdate;
    }

    private long createdate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getDatahash() {
        return datahash;
    }

    public void setDatahash(String datahash) {
        this.datahash = datahash;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", datahash='" + datahash + '\'' +
                ", data='" + data + '\'' +
                ", createdate=" + createdate +
                '}';
    }
}
