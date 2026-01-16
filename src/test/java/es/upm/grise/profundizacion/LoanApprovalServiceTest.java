package es.upm.grise.profundizacion;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LoanApprovalServiceTest {

    private final LoanApprovalService service = new LoanApprovalService();

    // P1: score < 500 -> REJECTED
    @Test
    void CaminoBasico1_scoreBelow500_rejected() {
        LoanApprovalService.Applicant applicant = new LoanApprovalService.Applicant(
            3000, // income
            499,  // score
            false, // hasDefaults
            false  // isVip
        );
        LoanApprovalService.Decision decision = service.evaluateLoan(applicant, 1000, 12);
        assertEquals(LoanApprovalService.Decision.REJECTED, decision);
    }

    // P2: 500<=score<650 & income>=2500 & !defaults & VIP & score>=600 -> APPROVED (VIP elevation)
    @Test
    void CaminoBasico2_midScoreIncomeHighNoDefaultsVipElevated_approved() {
        LoanApprovalService.Applicant applicant = new LoanApprovalService.Applicant(
            3000,
            600,
            false,
            true
        );
        LoanApprovalService.Decision decision = service.evaluateLoan(applicant, 1000, 12);
        assertEquals(LoanApprovalService.Decision.APPROVED, decision);
    }

    // P3: 500<=score<650 & income>=2500 & !defaults & !VIP -> MANUAL_REVIEW
    @Test
    void CaminoBasico3_midScoreIncomeHighNoDefaultsNotVip_manualReview() {
        LoanApprovalService.Applicant applicant = new LoanApprovalService.Applicant(
            3000,
            600,
            false,
            false
        );
        LoanApprovalService.Decision decision = service.evaluateLoan(applicant, 1000, 12);
        assertEquals(LoanApprovalService.Decision.MANUAL_REVIEW, decision);
    }

    // P4: 500<=score<650 & income>=2500 & !defaults & VIP but score<600 -> MANUAL_REVIEW
    @Test
    void CaminoBasico4_midScoreIncomeHighNoDefaultsVipScoreBelow600_manualReview() {
        LoanApprovalService.Applicant applicant = new LoanApprovalService.Applicant(
            3000,
            599,
            false,
            true
        );
        LoanApprovalService.Decision decision = service.evaluateLoan(applicant, 1000, 12);
        assertEquals(LoanApprovalService.Decision.MANUAL_REVIEW, decision);
    }

    // P5: 500<=score<650 & income<2500 -> REJECTED
    @Test
    void CaminoBasico5_midScoreIncomeLow_rejected() {
        LoanApprovalService.Applicant applicant = new LoanApprovalService.Applicant(
            2000,
            600,
            false,
            true
        );
        LoanApprovalService.Decision decision = service.evaluateLoan(applicant, 1000, 12);
        assertEquals(LoanApprovalService.Decision.REJECTED, decision);
    }

    // P6: 500<=score<650 & income>=2500 & hasDefaults -> REJECTED
    @Test
    void CaminoBasico6_midScoreIncomeHighWithDefaults_rejected() {
        LoanApprovalService.Applicant applicant = new LoanApprovalService.Applicant(
            3000,
            600,
            true,
            true
        );
        LoanApprovalService.Decision decision = service.evaluateLoan(applicant, 1000, 12);
        assertEquals(LoanApprovalService.Decision.REJECTED, decision);
    }

    // P7: score>=650 & amount<=income*8 -> APPROVED
    @Test
    void CaminoBasico7_highScoreAffordableAmount_approved() {
        LoanApprovalService.Applicant applicant = new LoanApprovalService.Applicant(
            3000,
            700,
            false,
            false
        );
        int amountRequested = 3000 * 8; // exactly threshold
        LoanApprovalService.Decision decision = service.evaluateLoan(applicant, amountRequested, 24);
        assertEquals(LoanApprovalService.Decision.APPROVED, decision);
    }

    // P8: score>=650 & amount>income*8 & !VIP -> MANUAL_REVIEW
    @Test
    void CaminoBasico8_highScoreAmountTooHighNotVip_manualReview() {
        LoanApprovalService.Applicant applicant = new LoanApprovalService.Applicant(
            3000,
            700,
            false,
            false
        );
        int amountRequested = 3000 * 8 + 1;
        LoanApprovalService.Decision decision = service.evaluateLoan(applicant, amountRequested, 24);
        assertEquals(LoanApprovalService.Decision.MANUAL_REVIEW, decision);
    }

    // P9: score>=650 & amount>income*8 & VIP & hasDefaults -> MANUAL_REVIEW
    @Test
    void CaminoBasico9_highScoreTooHighAmountVipWithDefaults_manualReview() {
        LoanApprovalService.Applicant applicant = new LoanApprovalService.Applicant(
            3000,
            700,
            true,
            true
        );
        int amountRequested = 3000 * 8 + 1;
        LoanApprovalService.Decision decision = service.evaluateLoan(applicant, amountRequested, 24);
        assertEquals(LoanApprovalService.Decision.MANUAL_REVIEW, decision);
    }

    // P10: score>=650 & amount>income*8 & VIP & !defaults & score>=600 -> APPROVED (VIP elevation)
    @Test
    void CaminoBasico10_highScoreTooHighAmountVipNoDefaultsElevated_approved() {
        LoanApprovalService.Applicant applicant = new LoanApprovalService.Applicant(
            3000,
            700,
            false,
            true
        );
        int amountRequested = 3000 * 8 + 1;
        LoanApprovalService.Decision decision = service.evaluateLoan(applicant, amountRequested, 24);
        assertEquals(LoanApprovalService.Decision.APPROVED, decision);
    }
}
