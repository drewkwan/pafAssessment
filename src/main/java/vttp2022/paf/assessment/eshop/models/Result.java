package vttp2022.paf.assessment.eshop.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Result {
    private String status;
    private int count;
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    public static Result create(SqlRowSet rs) {
        Result result = new Result();
        result.setStatus(rs.getString("status"));
        result.setCount(rs.getInt("count"));
        return result;
    }

    
}
