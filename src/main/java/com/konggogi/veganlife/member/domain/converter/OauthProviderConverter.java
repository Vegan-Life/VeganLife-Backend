package com.konggogi.veganlife.member.domain.converter;


import com.konggogi.veganlife.member.domain.oauth.OauthProvider;
import org.springframework.core.convert.converter.Converter;

public class OauthProviderConverter implements Converter<String, OauthProvider> {
    @Override
    public OauthProvider convert(String provider) {
        return OauthProvider.from(provider);
    }
}
