package com.getme.nft.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getme.nft.model.Nft;
import com.getme.nft.service.NftService;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NftServiceImpl implements NftService {

    @Value("${NFTS_URL}")
    private String nftsURL;

    private final OkHttpClient client;

    @Override
    public List<Nft> getNfts() {
        List<Nft> nftList = new ArrayList<>();
        Request request = new Request.Builder()
                .url(nftsURL)
                .header("Content-Type", "application/json")
                .build();
        try (Response resp  = client.newCall(request).execute()) {
            ResponseBody respBody = resp.body();
            if (respBody == null) {
                return nftList;
            }
            String body = respBody.string();
            ObjectMapper objectMapper = new ObjectMapper();
            nftList.addAll(Arrays.asList(objectMapper.readValue(body, Nft[].class)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return nftList;
    }

}
