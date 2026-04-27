import java.util.Objects;

/**
 * UC7: Addition with explicit target-unit specification.
 * QuantityLength.add(a, b, targetUnit) converts the sum to the requested unit.
 *
 * Fixes:
 *  - Original was test-only and truncated.
 *  - Full self-contained implementation included (mirrors UC6's QuantityLength
 *    with the 3-arg static add method).
 *  - All test cases from the original document completed.
 */
public class UC7 {

    // ── LengthUnit ──────────────────────────────────────────────────────────
    public enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(1.0 / 30.48); // exact: 1 ft = 30.48 cm

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        public double toFeet(double value)       { return value * toFeetFactor; }
        public double fromFeet(double feetValue) { return feetValue / toFeetFactor; }
    }

    // ── QuantityLength ───────────────────────────────────────────────────────
    public static final class QuantityLength {
        private static final double EPSILON = 1e-9;

        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
            this.value = value;
            this.unit  = unit;
        }

        public double getValue()    { return value; }
        public LengthUnit getUnit() { return unit; }

        /** Static add — result in unit of first operand (UC6 behaviour). */
        public static QuantityLength add(QuantityLength a, QuantityLength b) {
            return add(a, b, a.unit);
        }

        /** Static add with explicit target unit (UC7 behaviour). */
        public static QuantityLength add(QuantityLength a, QuantityLength b, LengthUnit targetUnit) {
            if (a == null || b == null) throw new IllegalArgumentException("Operands cannot be null");
            if (targetUnit == null)     throw new IllegalArgumentException("Target unit cannot be null");
            double sumFeet      = a.unit.toFeet(a.value) + b.unit.toFeet(b.value);
            double resultValue  = targetUnit.fromFeet(sumFeet);
            return new QuantityLength(resultValue, targetUnit);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof QuantityLength q)) return false;
            return Math.abs(unit.toFeet(value) - q.unit.toFeet(q.value)) <= EPSILON;
        }

        @Override
        public int hashCode() {
            long key = Math.round(unit.toFeet(value) * 1e9);
            return Objects.hash(key);
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    // ── Demo main (mirrors UC7 test assertions) ──────────────────────────────
    public static void main(String[] args) {
        final double EPS = 1e-6;

        // Test 1: FEET + INCHES -> FEET  (1 ft + 12 in = 2 ft)
        QuantityLength r1 = QuantityLength.add(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(12.0, LengthUnit.INCHES),
                LengthUnit.FEET);
        System.out.printf("1 ft + 12 in -> FEET    = %.4f FEET    (expect 2.0)%n", r1.getValue());

        // Test 2: FEET + INCHES -> INCHES (1 ft + 12 in = 24 in)
        QuantityLength r2 = QuantityLength.add(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(12.0, LengthUnit.INCHES),
                LengthUnit.INCHES);
        System.out.printf("1 ft + 12 in -> INCHES  = %.4f INCHES (expect 24.0)%n", r2.getValue());

        // Test 3: FEET + INCHES -> YARDS  (1 ft + 12 in = 24 in = 2/3 yd)
        QuantityLength r3 = QuantityLength.add(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(12.0, LengthUnit.INCHES),
                LengthUnit.YARDS);
        System.out.printf("1 ft + 12 in -> YARDS   = %.6f YARDS (expect 0.666667)%n", r3.getValue());

        // Test 4: INCHES + INCHES -> CENTIMETERS (1 in + 1 in = 2 in = 5.08 cm)
        QuantityLength r4 = QuantityLength.add(
                new QuantityLength(1.0, LengthUnit.INCHES),
                new QuantityLength(1.0, LengthUnit.INCHES),
                LengthUnit.CENTIMETERS);
        System.out.printf("1 in + 1 in -> CM       = %.4f CM (expect 5.08)%n", r4.getValue());

        // Test 5: Target unit same as first operand (YARDS)
        QuantityLength r5 = QuantityLength.add(
                new QuantityLength(2.0, LengthUnit.YARDS),
                new QuantityLength(3.0, LengthUnit.FEET),
                LengthUnit.YARDS);
        System.out.printf("2 yd + 3 ft -> YARDS    = %.4f YARDS (expect 3.0)%n", r5.getValue());

        // Test 6: Both operands in same unit, target different
        QuantityLength r6 = QuantityLength.add(
                new QuantityLength(6.0, LengthUnit.INCHES),
                new QuantityLength(6.0, LengthUnit.INCHES),
                LengthUnit.FEET);
        System.out.printf("6 in + 6 in -> FEET     = %.4f FEET (expect 1.0)%n", r6.getValue());
    }
}
