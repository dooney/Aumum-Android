package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.Special;

import retrofit.http.GET;

/**
 * Created by Administrator on 10/03/2015.
 */
public interface SpecialService {

    @GET(Constants.Http.URL_SPECIALS_FRAG)
    ListWrapper<Special> getList();
}
