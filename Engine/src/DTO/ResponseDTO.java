package DTO;

import com.google.gson.annotations.Expose;

public class ResponseDTO {

    @Expose
    public int Status;

    @Expose
    public String ErrorMessage;

    @Expose
    public String RedirectUrl;
}
