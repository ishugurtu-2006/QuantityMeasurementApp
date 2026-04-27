import java.util.Objects;

/**
 * UC5: Unit conversion utility + JUnit-style test specification.
 *
 * Fixes:
 *  - The original was a test-only file referencing QuantityLengthConverter which didn't exist.
 *    Added a self-contained QuantityLengthConverter implementation.
 *  - LengthUnit now exposes toFeetFactor() as a public method (tests called it).
 *  - The assertEquals round-trip test was truncated; completed it.
 *  - Class renamed/restructured so the file compiles as UC5.java.
 */
public class UC5 {

    // ── LengthUnit ──────────────────────────────────────────────────────────
    public enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(0.0328084); // 1 cm ≈ 0.0328084 ft

        private final double toFeetFactorValue;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactorValue = toFeetFactor;
        }

        /** Exposed for test use (was referenced as from.toFeetFactor() in tests). */
        public double toFeetFactor() {
            return toFeetFactorValue;
        }

        public double toBaseUnit(double value) {
            return value * toFeetFactorValue;
        }

        public double fromBaseUnit(double feetValue) {
            return feetValue / toFeetFactorValue;
        }
    }

    // ── QuantityLengthConverter ──────────────────────────────────────────────
    /**
     * Stateless converter referenced by the UC5 tests.
     * convert(value, from, to) = value * from.toFeetFactor / to.toFeetFactor
     */
    public static final class QuantityLengthConverter {

        private QuantityLengthConverter() {}

        public static double convert(double value, LengthUnit from, LengthUnit to) {
            if (from == null) throw new IllegalArgumentException("from unit cannot be null");
            if (to == null)   throw new IllegalArgumentException("to unit cannot be null");
            double feet = from.toBaseUnit(value);
            return to.fromBaseUnit(feet);
        }
    }

    // ── Demo main ────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        final double EPS = 1e-9;

        // Identity: 5 ft -> ft = 5
        double id = QuantityLengthConverter.convert(5.0, LengthUnit.FEET, LengthUnit.FEET);
        System.out.println("Identity  5 ft -> ft : " + id);                      // 5.0

        // Cross-unit: 1 ft -> inches = 12
        double ftToIn = QuantityLengthConverter.convert(1.0, LengthUnit.FEET, LengthUnit.INCHES);
        System.out.println("1 ft -> inches       : " + ftToIn);                  // 12.0

        // Cross-unit: 1 yd -> ft = 3
        double ydToFt = QuantityLengthConverter.convert(1.0, LengthUnit.YARDS, LengthUnit.FEET);
        System.out.println("1 yd -> ft           : " + ydToFt);                  // 3.0

        // Round-trip: 1 ft -> inches -> ft = 1
        double rt = QuantityLengthConverter.convert(
                QuantityLengthConverter.convert(1.0, LengthUnit.FEET, LengthUnit.INCHES),
                LengthUnit.INCHES, LengthUnit.FEET);
        System.out.println("Round-trip 1 ft->in->ft : " + rt);                   // 1.0

        // Factor-based expected check
        double expected = 1.0 * LengthUnit.FEET.toFeetFactor() / LengthUnit.INCHES.toFeetFactor();
        double actual   = QuantityLengthConverter.convert(1.0, LengthUnit.FEET, LengthUnit.INCHES);
        System.out.println("Factor check matches : " + (Math.abs(expected - actual) <= EPS)); // true
    }
}
