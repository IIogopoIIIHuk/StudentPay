package com.service;

import com.DTO.DocumentationResponseDTO;
import com.DTO.PaymentDTO;
import com.DTO.StudentPaymentDTO;
import com.entity.*;
import com.exception.AppError;
import com.repo.PaymentRepository;
import com.repo.StudentPaymentRepository;
import com.repo.StipendRepository;
import com.repo.StipendSettingsRepository;
import com.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final StudentPaymentRepository studentPaymentRepository;
    private final UserRepository userRepository;
    private final StipendService stipendService;

    public boolean hasPaymentForCurrentMonth() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        return paymentRepository.findByPaymentDateBetween(startOfMonth, endOfMonth).isPresent();
    }

    @Transactional
    public PaymentDTO calculateAndCreateMonthlyPayment() {
        if (hasPaymentForCurrentMonth()) {
            throw new IllegalStateException("Выплата за текущий месяц уже создана.");
        }

        List<User> students = userRepository.findByRoleName("ROLE_USER");
        List<StudentPayment> studentPayments = new ArrayList<>();
        double totalAmount = 0.0;

        Optional<StipendSettings> settingsOptional = stipendService.getStipendSettings();
        if (settingsOptional.isEmpty()) {
            throw new IllegalStateException("Настройки стипендии не найдены.");
        }
        StipendSettings settings = settingsOptional.get();

        for (User student : students) {
            StudentDetails details = student.getStudentDetails();
            if (details == null || !details.getHasStipend()) {
                continue;
            }

            Optional<Stipend> stipendOptional = stipendService.findByTypeName(details.getStipendType());
            if (stipendOptional.isEmpty()) {
                continue;
            }
            Stipend stipend = stipendOptional.get();

            double amount = stipend.getAmount();
            double bonus = details.getBonusAmount();

            double profkomDeduction = 0.0;
            if (student.isProfkomMember()) {
                profkomDeduction = amount * (settings.getProfkomDeductionPercent() / 100);
            }

            double brsmDeduction = 0.0;
            if (student.isBrsmMember()) {
                brsmDeduction = amount * (settings.getBrsmDeductionPercent() / 100);
            }

            double finalAmount = amount + bonus - profkomDeduction - brsmDeduction;

            StudentPayment studentPayment = new StudentPayment();
            studentPayment.setStudent(student);
            studentPayment.setAmount(finalAmount);
            studentPayments.add(studentPayment);
            totalAmount += finalAmount;
        }

        LocalDate paymentDate = LocalDate.now().withDayOfMonth(24);
        if (paymentDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            paymentDate = paymentDate.minusDays(1);
        } else if (paymentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            paymentDate = paymentDate.minusDays(2);
        }

        String paymentName = getPreviousMonthName() + " " + paymentDate.getYear();

        Payment newPayment = new Payment();
        newPayment.setPaymentName(paymentName);
        newPayment.setTotalAmount(totalAmount);
        newPayment.setPaymentDate(paymentDate);
        newPayment.setStatus("Не оплачено");

        Payment savedPayment = paymentRepository.save(newPayment);

        for (StudentPayment studentPayment : studentPayments) {
            studentPayment.setPayment(savedPayment);
        }
        studentPaymentRepository.saveAll(studentPayments);
        savedPayment.setStudentPayments(studentPayments);

        return PaymentDTO.fromEntity(savedPayment);
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(PaymentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<PaymentDTO> getPaymentDetails(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(PaymentDTO::fromEntity);
    }

    public List<PaymentDTO> getPaymentsByDate(int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        return paymentRepository.findByPaymentDateBetween(startOfMonth, endOfMonth).stream()
                .map(PaymentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getMyPaymentsByDate(Long studentId, int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        return paymentRepository.findAllByPaymentDateBetween(start, end).stream()
                .filter(p -> p.getStudentPayments().stream()
                        .anyMatch(sp -> sp.getStudent().getId().equals(studentId)))
                .map(p -> PaymentDTO.fromEntityForStudent(p, studentId))
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getAllMyPayments(Long studentId) {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getStudentPayments() != null &&
                        payment.getStudentPayments().stream().anyMatch(sp -> sp.getStudent().getId().equals(studentId)))
                .map(p -> PaymentDTO.fromEntityForStudent(p, studentId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePayment(Long paymentId) {
        paymentRepository.deleteById(paymentId);
    }

    @Transactional
    public Optional<PaymentDTO> updatePaymentStatus(Long paymentId, String newStatus) {
        return paymentRepository.findById(paymentId)
                .map(payment -> {
                    payment.setStatus(newStatus);
                    return PaymentDTO.fromEntity(paymentRepository.save(payment));
                });
    }

    private String getPreviousMonthName() {
        LocalDate now = LocalDate.now();
        LocalDate previousMonth = now.minusMonths(1);
        return previousMonth.getMonth().name();
    }

    public DocumentationResponseDTO getDocumentationData(int month, int year, Long studentId) {
        YearMonth ym = YearMonth.of(year, month);

        Payment payment = paymentRepository.findAllByPaymentDateBetween(ym.atDay(1), ym.atEndOfMonth())
                .stream().findFirst()
                .orElseThrow(() -> new NoSuchElementException("Ведомость за " + month + "." + year + " не найдена."));

        if (studentId == null) {
            List<DocumentationResponseDTO.StudentSummaryDTO> summary = payment.getStudentPayments().stream()
                    .map(sp -> new DocumentationResponseDTO.StudentSummaryDTO(
                            sp.getStudent().getId(),
                            sp.getStudent().getName(),
                            sp.getAmount()))
                    .toList();

            return DocumentationResponseDTO.builder()
                    .paymentDate(payment.getPaymentDate())
                    .totalAmount(payment.getTotalAmount())
                    .students(summary)
                    .build();
        } else {
            StudentPayment sp = payment.getStudentPayments().stream()
                    .filter(p -> p.getStudent().getId().equals(studentId))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Данные по студенту с ID " + studentId + " за этот период отсутствуют."));

            User u = sp.getStudent();

            if (u.getStudentDetails() == null || u.getStudentDetails().getStipendType() == null) {
                throw new IllegalStateException("У студента не настроены данные о стипендии.");
            }

            Stipend stipend = stipendService.findByTypeName(u.getStudentDetails().getStipendType())
                    .orElseThrow(() -> new NoSuchElementException("Тип стипендии '" + u.getStudentDetails().getStipendType() + "' не найден в справочнике."));

            StipendSettings settings = stipendService.getStipendSettings()
                    .orElseThrow(() -> new NoSuchElementException("Настройки процентов (Профком/БРСМ) не заданы."));

            double base = stipend.getAmount();
            double prof = u.isProfkomMember() ? base * (settings.getProfkomDeductionPercent() / 100) : 0;
            double brsm = u.isBrsmMember() ? base * (settings.getBrsmDeductionPercent() / 100) : 0;

            return DocumentationResponseDTO.builder()
                    .paymentDate(payment.getPaymentDate())
                    .student(new DocumentationResponseDTO.StudentInfoDTO(u.getId(), u.getName(), "ПИ-201"))
                    .calculations(DocumentationResponseDTO.CalculationDetailsDTO.builder()
                            .baseScholarship(base)
                            .bonus(u.getStudentDetails().getBonusAmount())
                            .profcomPercent(settings.getProfkomDeductionPercent())
                            .brsmPercentage(settings.getBrsmDeductionPercent())
                            .profcomDeduction(prof)
                            .brsmDeduction(brsm)
                            .totalAmount(sp.getAmount())
                            .build())
                    .build();
        }
    }
}