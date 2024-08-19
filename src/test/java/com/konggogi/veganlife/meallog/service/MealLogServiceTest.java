package com.konggogi.veganlife.meallog.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

import com.konggogi.veganlife.global.AwsS3Uploader;
import com.konggogi.veganlife.global.domain.AwsS3Folders;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.mealdata.service.MealDataQueryService;
import com.konggogi.veganlife.meallog.controller.dto.request.MealAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogModifyRequest;
import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.meallog.domain.mapper.*;
import com.konggogi.veganlife.meallog.fixture.MealFixture;
import com.konggogi.veganlife.meallog.fixture.MealImageFixture;
import com.konggogi.veganlife.meallog.fixture.MealLogFixture;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.IntakeNotifyService;
import com.konggogi.veganlife.member.service.MemberQueryService;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class MealLogServiceTest {

    @Mock MemberQueryService memberQueryService;
    @Mock MealDataQueryService mealDataQueryService;
    @Mock MealLogQueryService mealLogQueryService;
    @Mock MealLogRepository mealLogRepository;
    @Mock IntakeNotifyService intakeNotifyService;
    @Spy MealLogMapper mealLogMapper = new MealLogMapperImpl();
    @Spy MealMapper mealMapper = new MealMapperImpl();
    @Spy MealImageMapper mealImageMapper = new MealImageMapperImpl();
    @Mock AwsS3Uploader awsS3Uploader;
    @InjectMocks MealLogService mealLogService;

    Member member = Member.builder().id(1L).email("test123@test.com").build();
    List<MealData> mealData =
            List.of(
                    MealDataFixture.TOTAL_AMOUNT.get(1L, member),
                    MealDataFixture.TOTAL_AMOUNT.get(2L, member),
                    MealDataFixture.TOTAL_AMOUNT.get(3L, member));
    List<MealAddRequest> mealAddRequests =
            mealData.stream()
                    .map(m -> new MealAddRequest(100, 100, 10, 10, 10, m.getId()))
                    .toList();

    List<String> imageUrls = List.of("image1.png", "image2.png", "image3.png");

    @Test
    @DisplayName("식사 기록 저장")
    void mealLogAddTest() {
        // given
        MealLogAddRequest mealLogAddRequest =
                new MealLogAddRequest(MealType.BREAKFAST, mealAddRequests);
        given(memberQueryService.search(1L)).willReturn(member);
        doNothing().when(intakeNotifyService).notifyIfOverIntake(1L);
        mealData.forEach(m -> given(mealDataQueryService.search(m.getId())).willReturn(m));
        List<MultipartFile> images =
                List.of(
                        new MockMultipartFile(
                                "images",
                                "image1.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "image1.png".getBytes()));
        List<String> imageUrls = List.of("image1.png", "image2.jpeg");
        willReturn(imageUrls).given(awsS3Uploader).uploadFiles(eq(AwsS3Folders.LIFE_CHECK), any());
        // when
        mealLogService.add(mealLogAddRequest, images, member.getId());
        // then
        then(memberQueryService).should(times(1)).search(member.getId());
        mealData.forEach(m -> then(mealDataQueryService).should(times(1)).search(m.getId()));
        then(mealLogRepository).should(times(1)).save(any(MealLog.class));
    }

    @Test
    @DisplayName("식사 기록 수정")
    void mealLogModifyTest() {
        // given
        MealLogModifyRequest mealLogModifyRequest = new MealLogModifyRequest(mealAddRequests);
        List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        List<MealImage> mealImages =
                IntStream.range(0, 3).mapToObj(idx -> MealImageFixture.DEFAULT.get()).toList();
        MealLog mealLog = MealLogFixture.BREAKFAST.get(meals, mealImages, member);
        given(mealLogQueryService.search(1L)).willReturn(mealLog);
        doNothing().when(intakeNotifyService).notifyIfOverIntake(1L);
        mealData.forEach(m -> given(mealDataQueryService.search(m.getId())).willReturn(m));
        List<MultipartFile> images =
                List.of(
                        new MockMultipartFile(
                                "images",
                                "image1.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "image1.png".getBytes()));
        List<String> imageUrls = List.of("image1.png", "image2.jpeg");
        willReturn(imageUrls).given(awsS3Uploader).uploadFiles(eq(AwsS3Folders.LIFE_CHECK), any());
        // when
        mealLogService.modify(1L, mealLogModifyRequest, images);
        // then
        then(mealLogQueryService).should(times(1)).search(1L);
        mealData.forEach(m -> then(mealDataQueryService).should(times(1)).search(m.getId()));
    }

    @Test
    @DisplayName("식사 기록 삭제")
    void mealLogRemoveTest() {
        // given
        List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        List<MealImage> mealImages =
                imageUrls.stream().map(MealImageFixture.DEFAULT::getWithImageUrl).toList();
        MealLog mealLog = MealLogFixture.DINNER.get(1L, meals, mealImages, member);
        given(mealLogQueryService.search(1L)).willReturn(mealLog);
        // when
        mealLogService.remove(1L);
        // then
        then(mealLogRepository).should(times(1)).delete(mealLog);
    }

    @Test
    @DisplayName("회원 Id로 MealLog 모두 삭제")
    void removeAllTest() {
        // given
        given(memberQueryService.search(1L)).willReturn(member);
        // when, then
        assertDoesNotThrow(() -> mealLogService.removeAll(1L));
        then(mealLogRepository).should().deleteAllByMemberId(anyLong());
    }
}
