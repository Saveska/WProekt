package com.wproekt.service.impl;

import com.wproekt.service.UtilService;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

@Service
public class UtilServiceImplementation implements UtilService {
    @Override
    public String cleanHtml(String html) {

        return Jsoup.clean(html, Safelist.none());
    }
}
