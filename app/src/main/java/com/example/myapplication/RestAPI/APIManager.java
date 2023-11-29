package com.example.myapplication.RestAPI;

import com.example.myapplication.LoadingActivity;
import com.example.myapplication.Model.Device;
import com.example.myapplication.Model.Role;
import com.example.myapplication.Model.User;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;
import android.util.Log;
import android.widget.Toast;

public class APIManager {
    private static final APIClient apiClient = new APIClient();
    private static final APIInterface userAI = apiClient.getClient().create(APIInterface.class);

    public static void getUserInfo() {
        Call<User> call = userAI.getUserInfo();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("API CALL", response.code()+"");
                User.setMe(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("API CALL", t.getMessage().toString());
            }
        });
    }

    public static void getUserRoles() {
        Call<List<Role>> call = userAI.getUserRoles();

        try {
            Response<List<Role>> response = call.execute();
            assert response.body() != null;
            User.getMe().setUserRoles(response.body());
        } catch (IOException e) { e.printStackTrace(); }

    }
    public static void queryDevices(JsonObject body) {
        Call<List<Device>> call = userAI.queryDevices(body);
        try {
            Response<List<Device>> response = call.execute();
            if (response.isSuccessful() && response.code() == 200) {
                List<Device> deviceList = response.body();
                Device.setDevicesList(deviceList);
            } else {
                Device.setDevicesList(null);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
//    public static void getMap() {
//        Call<Map> call = userAI.getMap();
//        try {
//            Response<Map> response = call.execute();
//            if (response.isSuccessful()) { Map.setMapObj(response.body()); }
//        } catch (IOException e) { e.printStackTrace(); }
//    }
}