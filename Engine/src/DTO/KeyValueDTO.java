package DTO;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

public class KeyValueDTO extends ResponseDTO {


    @Expose
    public Map<String, Object> Values = new HashMap<String, Object>();

}
