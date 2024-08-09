package com.T82.ticket.config.grpc;

import com.T82.ticket.global.domain.entity.Seat;
import com.T82.ticket.global.domain.entity.Section;
import com.T82.ticket.global.domain.exception.SeatNotFoundException;
import com.T82.ticket.global.domain.exception.SectionNotFoundException;
import com.T82.ticket.global.domain.repository.SeatRepository;
import com.T82.ticket.global.domain.repository.SectionRepository;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.t82.seat.lib.SeatDetailRequest;
import org.t82.seat.lib.SeatDetailResponse;
import org.t82.seat.lib.SeatGrpc;

import java.util.ArrayList;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class GrpcServer extends SeatGrpc.SeatImplBase{
    private final GrpcUtil grpcUtil;
    private final SectionRepository sectionRepository;
    private final SeatRepository seatRepository;

    @Override
    public void getSeatDetail(SeatDetailRequest request, StreamObserver<SeatDetailResponse> responseObserver) {
        List<Seat> seats = new ArrayList<>();
        request.getSeatIdList().forEach(seatId -> {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(SeatNotFoundException::new);
            log.info("seatId" + seat.getSeatId());

            responseObserver.onNext(
                    SeatDetailResponse.newBuilder()
                            .setId(seatId)
                            .setSection(seat.getSection().getName())
                            .setRowNum(seat.getRowNum())
                            .setColNum(seat.getColNum())
                            .build()
            );
            seats.add(seat);
        });
        responseObserver.onCompleted();
        complete(seats);
    }

    @Transactional
    public void complete(List<Seat> seats) {
        seats.forEach(seat -> {
            Seat.SeatBook(seat);

            Section section = sectionRepository.findById(seat.getSection().getSectionId())
                    .orElseThrow(SectionNotFoundException:: new);

            Section.DecreaseInSectionSeats(section);
        });
    }
}
