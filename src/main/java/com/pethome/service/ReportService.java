package com.pethome.service;

import java.util.Map;

public interface ReportService {

    /** 经营看板汇总（在住数/入住率/今日洗护完成量/客单价） */
    Map<String, Object> summary();
}
