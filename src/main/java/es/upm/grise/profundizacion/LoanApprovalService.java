package es.upm.grise.profundizacion;

import java.util.Objects;

public class LoanApprovalService {

    /**
     * Método X: contiene decisiones encadenadas y compuestas para análisis estructural.
     *
     * Regla (simplificada):
     * - Entradas inválidas -> excepción
     * - Score < 500 -> REJECTED
     * - 500..649 -> si income alto y no tiene impagos -> MANUAL_REVIEW; si no -> REJECTED
     * - >= 650 -> si amount <= income*8 -> APPROVED; si no -> MANUAL_REVIEW
     * - Además, si el cliente es VIP y score>=600 y no tiene impagos -> se eleva a APPROVED si estaba en MANUAL_REVIEW
     */
    public Decision evaluateLoan(
            Applicant applicant,
            int amountRequested,
            int termMonths
    ) {
        // [1] Entrada al método
        validate(applicant, amountRequested, termMonths);

        // [2] Inicialización de variables
        int score = applicant.creditScore();
        boolean hasDefaults = applicant.hasRecentDefaults();
        int income = applicant.monthlyIncome();

        // [3] Declaración de variable de salida
        Decision decision;

        // [4] Decisión principal por score
        if (score < 500) { // [4a] Score bajo
            decision = Decision.REJECTED; // [5] Rechazo directo
        } else if (score < 650) { // [4b] Score medio
            // [6] Decisión por ingresos y impagos
            if (income >= 2500 && !hasDefaults) { // [6a] Ingresos altos y sin impagos
                decision = Decision.MANUAL_REVIEW; // [7] Revisión manual
            } else {
                decision = Decision.REJECTED; // [8] Rechazo
            }
        } else { // [4c] Score alto
            // [9] Decisión por relación amount/income
            if (amountRequested <= income * 8) { // [9a] Cantidad razonable
                decision = Decision.APPROVED; // [10] Aprobación directa
            } else {
                decision = Decision.MANUAL_REVIEW; // [11] Revisión manual
            }
        }

        // [12] Regla VIP para elevar MANUAL_REVIEW a APPROVED
        if (decision == Decision.MANUAL_REVIEW
                && applicant.isVip()
                && score >= 600
                && !hasDefaults) {
            decision = Decision.APPROVED; // [13] Elevación a aprobado
        }

        return decision; // [14] Salida del método
    }

    private void validate(Applicant applicant, int amountRequested, int termMonths) {
        Objects.requireNonNull(applicant, "applicant cannot be null");
        if (amountRequested <= 0) {
            throw new IllegalArgumentException("amountRequested must be > 0");
        }
        if (termMonths < 6 || termMonths > 84) {
            throw new IllegalArgumentException("termMonths must be between 6 and 84");
        }
        if (applicant.monthlyIncome() <= 0) {
            throw new IllegalArgumentException("monthlyIncome must be > 0");
        }
        if (applicant.creditScore() < 0 || applicant.creditScore() > 850) {
            throw new IllegalArgumentException("creditScore must be between 0 and 850");
        }
    }

    public enum Decision {
        APPROVED, MANUAL_REVIEW, REJECTED
    }

    public record Applicant(int monthlyIncome, int creditScore, boolean hasRecentDefaults, boolean isVip) { }
}

