package com.antimobile.retrofit;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by android2 on 12/19/2016.
 */

public class UserDetails {

    private Context mContext;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    private static final String MyPREFERENCES = "loginPrefs" ;
    private static final String NAME_KEY = "nameKey";
    private static final String ID_KEY = "idKey";
    private static final String TYPE = "type";
    private static final String EMAIL_KEY = "email";
    private static final String PHONE_KEY = "phone";
    private static final String CITY_KEY = "cityKey";

    private static final String PRODUCT_ID = "productId";
    private static final String VECHILE_REGISTARTION = "registrationNo";
    private static final String SERVICE_LIST = "serviceList";
    private static final String LIST_SERVICE = "listService";
    private static final String ORDER_ID = "orderID";
    private static final String CATEGORY = "category";
    private static final String BRAND = "brand";


    private static final String loggedIn = "isloggedIn";

    public UserDetails(Context context){
        this.mContext = context;
        mPref = mContext.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        mEditor = mPref.edit();
    }
    public void setCategory(String category){
        mEditor.putString(CATEGORY,category);
        mEditor.apply();
    }

    public String getCategory(){
        return mPref.getString(CATEGORY,"1");
    }

    public void setBrand(String brand){
        mEditor.putString(BRAND,brand);
        mEditor.apply();
    }

    public String getBrand(){
        return mPref.getString(BRAND,"");
    }




    public void setCity(String city){
        mEditor.putString(CITY_KEY,city);
        mEditor.apply();
    }

    public String getCity(){
        return mPref.getString(CITY_KEY,"empty");
    }

    public void setService(String service){
        mEditor.putString(SERVICE_LIST,service);
        mEditor.apply();
    }

    public String getService(){
        return mPref.getString(SERVICE_LIST,"empty");
    }

    public void setListService(String listservice){
        mEditor.putString(LIST_SERVICE,listservice);
        mEditor.apply();
    }

    public String getListService(){
        return mPref.getString(LIST_SERVICE,"empty");
    }

    public void setVechile(String vechile){
        mEditor.putString(VECHILE_REGISTARTION,vechile);
        mEditor.apply();
    }

    public String getVechile(){
        return mPref.getString(VECHILE_REGISTARTION,"empty");
    }


    public void setOrder(String order){
        mEditor.putString(ORDER_ID,order);
        mEditor.apply();
    }

    public String getOrder(){
        return mPref.getString(ORDER_ID,"empty");
    }

    public void setName(String name){
        mEditor.putString(NAME_KEY,name);
        mEditor.apply();
    }

    public String getName(){
        return mPref.getString(NAME_KEY,"empty");
    }




    public boolean isLoggedIn(){
        return mPref.getBoolean(loggedIn,false);
    }

    public void setLoggedIn(){
        mEditor.putBoolean(loggedIn,true);
        mEditor.apply();
    }


    public void logoutUser(){
        mEditor.clear();
        mEditor.apply();
    }

    public String getUserId() {
        return mPref.getString(ID_KEY, "empty");
    }

    public void setUserId(String mid){
        mEditor.putString(TYPE,mid);
        mEditor.apply();
    }

    public String getType() {
        return mPref.getString(TYPE, "empty");
    }

    public void setType(String mid){
        mEditor.putString(TYPE,mid);
        mEditor.apply();
    }

    public void setMobile(String name){
        mEditor.putString(PHONE_KEY,name);
        mEditor.apply();
    }

    public String getMobile(){
        return mPref.getString(PHONE_KEY,"empty");
    }

    public void setEmailKey(String name){
        mEditor.putString(EMAIL_KEY,name);
        mEditor.apply();
    }

    public String getEmailKey(){
        return mPref.getString(EMAIL_KEY,"empty");
    }

    public void setProductId(String name){
        mEditor.putString(PRODUCT_ID,name);
        mEditor.apply();
    }

    public String getProductId(){
        return mPref.getString(PRODUCT_ID,"empty");
    }

    public void setLanguage(Context context, int language) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("language", language).apply();
    }

    public static int getLanguage(Context context) {
        if (context != null) {
            return PreferenceManager.getDefaultSharedPreferences(context).getInt("language", 1);
        }
        return 1;
    }



}
