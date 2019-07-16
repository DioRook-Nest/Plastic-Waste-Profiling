package com.spearow.deepblue;

import com.spearow.deepblue.Remote.IGoogleAPIService;
import com.spearow.deepblue.Remote.RetrofitClient;

public class Common {
    private static final String GOOGLE_API_URL="https://maps.googleapis.com/maps/api/place/";

    public static IGoogleAPIService getGoogleApiService(){
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }
}
