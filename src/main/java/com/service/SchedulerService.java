package com.service;

import com.entity.StudentDetails;
import com.repo.StudentDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final StudentDetailsRepository studentDetailsRepository;

    @Scheduled(cron = "0 0 0 1 * ?") // Запускается в полночь первого числа каждого месяца
    @Transactional
    public void resetStudentData() {
        log.info("Запуск задачи сброса данных студентов...");
        List<StudentDetails> allStudentsDetails = studentDetailsRepository.findAll();
        for (StudentDetails details : allStudentsDetails) {
            details.setAbsencesHours(0);
            details.setBonusAmount(0.0);
        }
        studentDetailsRepository.saveAll(allStudentsDetails);
        log.info("Данные студентов успешно сброшены.");
    }
}