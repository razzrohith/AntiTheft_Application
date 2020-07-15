package com.antimobile.retrofit;


import com.antimobile.model.update;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by FAMILY on 14-12-2017.
 */

public interface RetrofitService {

    @POST(APIUrls.LOGIN)
    @FormUrlEncoded
    Call<update>update(@Field("mobile") String mobile, @Field("location") String location, @Field("image") String image);



}
