package com.example.campus.mapper;

import com.example.campus.entity.ReservationRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教室预约申请Mapper接口
 */
@Mapper
public interface ReservationRequestMapper {
    ReservationRequest selectById(@Param("requestId") Long requestId);
    List<ReservationRequest> selectByRequesterId(@Param("requesterId") Long requesterId,
                                                  @Param("status") Integer status,
                                                  @Param("dateFrom") java.time.LocalDate dateFrom);
    List<ReservationRequest> selectPending();
    List<ReservationRequest> selectByRoomIdAndTimeRange(@Param("roomId") Long roomId,
                                                         @Param("startTime") LocalDateTime startTime,
                                                         @Param("endTime") LocalDateTime endTime);
    int insert(ReservationRequest request);
    int update(ReservationRequest request);
    int updateStatus(@Param("requestId") Long requestId, @Param("status") Integer status);
    int deleteById(@Param("requestId") Long requestId);
}

