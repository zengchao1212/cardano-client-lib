package com.bloxbean.cardano.client.backend.impl.blockfrost.service;

import com.bloxbean.cardano.client.backend.model.Result;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.time.Duration;

public class BFBaseService {
    private final static Logger LOG = LoggerFactory.getLogger(BFBaseService.class);

    private String baseUrl;
    private String projectId;
    private Retrofit retrofit;

    public BFBaseService(String baseUrl, String projectId) {
        this.baseUrl=baseUrl;
        this.projectId=projectId;
    }

    public BFBaseService(String projectId,Retrofit retrofit) {
        this.baseUrl = retrofit.baseUrl().toString();
        this.projectId = projectId;
        this.retrofit=retrofit;
        if(LOG.isDebugEnabled()) {
            LOG.debug("Blockfrost URL : " + baseUrl);
        }
    }

    protected Retrofit getRetrofit() {
        if(retrofit!=null){
            return retrofit;
        }
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getProjectId() {
        return projectId;
    }

    protected  <T> Result<T> processResponse(Response<T> response) throws IOException {

            if(response.isSuccessful())
                return Result.success(response.toString()).withValue(response.body()).code(response.code());
            else
                return Result.error(response.errorBody().string()).code(response.code());

    }

}
