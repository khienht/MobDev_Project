package com.example.myapplication.RestAPI;

import com.example.myapplication.Model.Device;
import com.example.myapplication.Model.Role;
import com.example.myapplication.Model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {
    @FormUrlEncoded
    @POST("auth/realms/master/protocol/openid-connect/token")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/x-www-form-urlencoded",
    })
    Call<ResponseBody> getToken(@Field("client_id") String client_id,
                                @Field("username") String username,
                                @Field("password") String password,
                                @Field("grant_type") String grant_type);
    // Get user info
    @GET("api/master/user/user")
    Call<User> getUserInfo();

    // Get user roles
    @GET("api/master/user/userRoles")
    Call<List<Role>> getUserRoles();

    // Get all devices
    @Headers("Content-Type: application/json")
    @POST("api/master/asset/query")
    Call<List<Device>> queryDevices(@Body JsonObject body);

}
