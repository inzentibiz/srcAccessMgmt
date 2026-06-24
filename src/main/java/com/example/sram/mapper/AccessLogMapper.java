package com.example.sram.mapper;

import com.example.sram.dto.AccessLog;
import com.example.sram.dto.AccessLogRequest;
import com.example.sram.dto.AccessSearch;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 출입 기록 MyBatis 매퍼.
 * SQL 은 resources/mapper/AccessLogMapper.xml 에 정의.
 */
@Mapper
public interface AccessLogMapper {

    /** 조건 검색 (관리자 대시보드) */
    List<AccessLog> findLogs(AccessSearch search);

    /** 오늘 출입 기록 (최신순) */
    List<AccessLog> findTodayLogs();

    /** 단건 조회 */
    AccessLog findById(Long id);

    /** 출입 기록 등록 (생성된 id 를 request 가 아닌 AccessLog 로 받기 위해 selectKey 사용) */
    int insertLog(AccessLogRequest req);

    /** 오늘 출입 건수 */
    long countToday();

    /** 어제 출입 건수 */
    long countYesterday();

    /** 누적 총 건수 */
    long countTotal();
}
