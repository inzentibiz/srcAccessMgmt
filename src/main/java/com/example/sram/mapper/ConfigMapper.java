package com.example.sram.mapper;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 시스템 설정(sram_config) 매퍼 — key/value 저장소.
 */
public interface ConfigMapper {

    /** 설정값 조회 (없으면 null) */
    String selectValue(@Param("key") String key);

    /** 설정 변경 시각 조회 (없으면 null) */
    LocalDateTime selectUpdatedAt(@Param("key") String key);

    /** 설정값 저장(없으면 insert, 있으면 update) */
    int upsert(@Param("key") String key, @Param("value") String value);
}
