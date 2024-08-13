package com.T82.ticket.config.grpc;

import com.T82.common_exception.exception.seat.SeatNotFoundException;
import com.T82.common_exception.exception.seat.SectionNotFoundException;
import com.T82.ticket.global.domain.entity.Seat;
import com.T82.ticket.global.domain.entity.Section;
import com.T82.ticket.global.domain.repository.SeatRepository;
import com.T82.ticket.global.domain.repository.SectionRepository;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerSecurityAutoConfiguration;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.t82.seat.lib.SeatDetailRequest;
import org.t82.seat.lib.SeatDetailResponse;
import org.t82.seat.lib.SeatGrpc;

import java.util.ArrayList;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
@Slf4j
@EnableAutoConfiguration(exclude = {GrpcServerSecurityAutoConfiguration.class})
public class GrpcServer extends SeatGrpc.SeatImplBase{
    private final GrpcUtil grpcUtil;
    private final SectionRepository sectionRepository;
    private final SeatRepository seatRepository;

    @Override
    public void getSeatDetail(SeatDetailRequest request, StreamObserver<SeatDetailResponse> responseObserver) {
        List<Seat> seats = new ArrayList<>();
        List<Section> sections = new ArrayList<>();
        request.getSeatIdList().forEach(seatId -> {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(SeatNotFoundException::new);
            log.info("seatId" + seat.getSeatId());


            Section section = sectionRepository.findById(seat.getSection().getSectionId())
                    .orElseThrow(SectionNotFoundException:: new);


            SeatDetailResponse reply = SeatDetailResponse.newBuilder()
                    .setId(seatId.longValue())
                    .setSection(section.getName())
                    .setRowNum(seat.getRowNum().intValue())
                    .setColNum(seat.getColNum().intValue())
                    .build();
            log.info("reply : {}", reply);
            responseObserver.onNext(reply);

            seats.add(seat);

            sections.add(section);
        });
        responseObserver.onCompleted();
        complete(seats, sections);
    }

    @Transactional
    public void complete(List<Seat> seats, List<Section> sections) {
        seats.forEach(Seat::SeatBook);
        sections.forEach(Section::DecreaseInSectionSeats);
    }
}
